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

package me.lucko.luckperms.commands.group.subcommands;

import me.lucko.luckperms.LuckPermsPlugin;
import me.lucko.luckperms.commands.CommandResult;
import me.lucko.luckperms.commands.Predicate;
import me.lucko.luckperms.commands.Sender;
import me.lucko.luckperms.commands.SubCommand;
import me.lucko.luckperms.constants.Message;
import me.lucko.luckperms.constants.Permission;
import me.lucko.luckperms.data.LogEntry;
import me.lucko.luckperms.exceptions.ObjectAlreadyHasException;
import me.lucko.luckperms.groups.Group;
import me.lucko.luckperms.utils.ArgumentChecker;

import java.util.List;

public class GroupAddSuffix extends SubCommand<Group> {
    public GroupAddSuffix() {
        super("addsuffix", "Adds a suffix to the group", "/%s group <group> addsuffix <priority> <suffix> [server] [world]",
                Permission.GROUP_ADDSUFFIX, Predicate.notInRange(2, 4));
    }

    @Override
    public CommandResult execute(LuckPermsPlugin plugin, Sender sender, Group group, List<String> args, String label) {
        final String suffix = args.get(1).replace("{SPACE}", " ");
        int priority;
        try {
            priority = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            Message.META_INVALID_PRIORITY.send(sender, args.get(0));
            return CommandResult.INVALID_ARGS;
        }

        final String node = "suffix." + priority + "." + ArgumentChecker.escapeCharacters(suffix);

        try {
            if (args.size() >= 3) {
                final String server = args.get(2).toLowerCase();
                if (ArgumentChecker.checkServer(server)) {
                    Message.SERVER_INVALID_ENTRY.send(sender);
                    return CommandResult.INVALID_ARGS;
                }

                if (args.size() == 3) {
                    group.setPermission(node, true, server);
                    Message.ADDSUFFIX_SERVER_SUCCESS.send(sender, group.getDisplayName(), suffix, priority, server);
                    LogEntry.build().actor(sender).acted(group)
                            .action("addsuffix " + priority + " " + args.get(1) + " " + server)
                            .build().submit(plugin, sender);
                } else {
                    final String world = args.get(3).toLowerCase();
                    group.setPermission(node, true, server, world);
                    Message.ADDSUFFIX_SERVER_WORLD_SUCCESS.send(sender, group.getDisplayName(), suffix, priority, server, world);
                    LogEntry.build().actor(sender).acted(group)
                            .action("addsuffix " + priority + " " + args.get(1) + " " + server + " " + world)
                            .build().submit(plugin, sender);
                }

            } else {
                group.setPermission(node, true);
                Message.ADDSUFFIX_SUCCESS.send(sender, group.getDisplayName(), suffix, priority);
                LogEntry.build().actor(sender).acted(group)
                        .action("addsuffix " + priority + " " + args.get(1))
                        .build().submit(plugin, sender);
            }

            save(group, sender, plugin);
            return CommandResult.SUCCESS;
        } catch (ObjectAlreadyHasException e) {
            Message.ALREADY_HAS_SUFFIX.send(sender, group.getDisplayName());
            return CommandResult.STATE_ERROR;
        }
    }
}
