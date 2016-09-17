/*
 * Copyright (c) 2016 Lucko (Luck) <luck@lucko.me>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms;

import lombok.Getter;
import me.lucko.luckperms.utils.PermissionCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BungeePlayerCache {

    private final PermissionCalculator calculator;

    @Getter
    private final Map<String, Boolean> permissions = new ConcurrentHashMap<>();

    public BungeePlayerCache(LuckPermsPlugin plugin, String name) {
        List<PermissionCalculator.PermissionProcessor> processors = new ArrayList<>(2);
        processors.add(new PermissionCalculator.MapProcessor(permissions));
        processors.add(new PermissionCalculator.WildcardProcessor(permissions));

        calculator = new PermissionCalculator(plugin, name, plugin.getConfiguration().getDebugPermissionChecks(), processors);
    }

    public void invalidateCache() {
        calculator.invalidateCache();
    }

    public boolean getPermissionValue(String permission) {
        return calculator.getPermissionValue(permission).asBoolean();
    }
}
