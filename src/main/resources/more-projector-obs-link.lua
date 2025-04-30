--[[
    Jingle More Projectors Plugin OBS Link v1.0.0

    The puropse of this OBS link script is to automatically open the projectors listed in Jingle More Projectors Plugin.

    LICENSE BELOW:

    MIT License

    Copyright (c) 2025 Naturean

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
]]

obs = obslua

---- Variables ----

more_projectors_dir = os.getenv("UserProfile"):gsub("\\", "/") .. "/.config/Jingle/more-projectors-plugin/"

timers_activated = false
last_states = {}
last_projector_request = 'N'

---- File Functions ----

function read_file_lines(filename)
    local rfile = io.open(filename, "r")
    if rfile == nil then
        return nil
    end

    local lines = {}
    for line in rfile:lines() do
        lines[#lines + 1] = line
    end

    rfile:close()

    return lines
end

function get_state_file_string()
    local success, result = pcall(read_file_lines, more_projectors_dir .. "obs-link-state")
    if success then
        return result
    end
    return nil
end

---- Misc Functions ----

function split_string(input_string, split_char)
    -- https://www.cnblogs.com/lucktomato/p/15234559.html
    local out = {}
    input_string.gsub(input_string, "[^" .. split_char .. "]+", function (str)
        table.insert(out, str)
    end)
    return out
end

---- OBS Functions ----

function get_scene(name)
    local source = obs.obs_get_source_by_name(name)
    if source == nil then
        return nil
    end
    local scene = obs.obs_scene_from_source(source)
    obs.obs_source_release(source)
    return scene
end

function scene_exists(name)
    return get_scene(name) ~= nil
end

---- Script Functions ----

function script_description()
    return
    [[
    <h1>More Projectors OBS Link</h1>
    <p>Links OBS to Jingle More Projectors Plugin.</p>
    <p>This script is for automatically opening projectors.</p>
    ]]
end

function script_load()
    last_states = get_state_file_string()
end

function script_update(settings)
    if timers_activated then
        return
    end

    timers_activated = true
    obs.timer_add(loop, 20)
end

function loop()
    local states = get_state_file_string()

    if (states == last_states or states == nil or states == {}) then
        return
    end

    last_states = states

    for i, state in pairs(states) do
        local state_args = split_string(state, '\t')

        local shouldOpen = state_args[1]
        local projector_name = state_args[2]
        local projector_request = state_args[3]

        if shouldOpen == 'Y' and projector_request ~= last_projector_request then
            last_projector_request = projector_request
            if projector_request ~= 'N' and scene_exists(projector_name) then
                obs.obs_frontend_open_projector("Scene", -1, "", projector_name)
            end
        end
    end
end