# Lost Keys

required on both server and client (although it shouldnt crash anything if only client/only server has it)

## Server Side Commands:

it adds a server side command `/lost_keys:override <player> <binding> <key>` that temporarily (until the player relogs) replaces the key for a given binding

for instance, `/lost_keys:override Steve key.forward key.keyboard.b` will mean Steve has to press B to walk forwards instead of W (or whatever they have it set to)

the `<key>` can also be set to whatever the value of another binding is, so `/lost_keys:override Steve key.drop key.forward` will mean whenever Steve walks forwards, they also drop whatever they're holding

to clear an override, set the `<key>` to `default` (or itself), to disable a key, set the `<key>` to `none` (or any invalid key), to force a `<key>` to be pressed, set it to `pressed`

the `<binding>` can also be `all`, which can be used to clear all overrides at once: `/lost_keys:override <player> all default`, disable all keys: `/lost_keys:override <player> all none` (then specific keys can be enabled by setting them to themselves) , or cause a mess: `/lost_keys:override <player> all key.keyboard.a`

you can also set keybindings to run commands, like so: `/lost_keys:command <player> <binding> <command>`, the command does not include the /, so: `/lost_keys:command @s key.forward say hi`, and the binding cannot be keyboard keys. commands are run by the client, so commands that require op wont work on an unopped player

## Client Side Commands:

it also adds two client side commands, `/lost_keys:lognext`, which tells you the name of the key you press next (and binding if there is one), so you can easily figure out how to override what you want

and `/lost_keys:list`, which tells you all applied overrides

## Notes:

not all keys work, because minecraft is inconsistent, but all the important ones like movement and attacking do. Likewise some keys will not work properly with all overrides

### Plugin Version:
the permission node is `lost_keys.override`, and it only works with specifically mentioned players and @a - so `/lost_keys:override Steve key.forward none` works, but `/lost_keys:override @p key.forward none` or `/execute as Steve run lost_keys:override @s key.forward none` do not! this is an important difference!