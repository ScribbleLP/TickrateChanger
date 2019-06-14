function initializeCoreMod() {
    return {
        'coremodone': {
            'target': {
                'type': 'CLASS',
                'name': 'paulscode.sound.SoundSystem'
            },
            'transformer': function(classNode) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes')

                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
                var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode')
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode')
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode')
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList')

                var api = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                var methods = classNode.methods;

                for(m in methods) {
                    var method = methods[m];
                    if (method.name.equals("setPitch") && method.desc.equals("(Ljava/lang/String;F)V")) {
                        var list = method.instructions;

                        var newCode = new InsnList();
                        newCode.add(new VarInsnNode(Opcodes.FLOAD, 2));
                        newCode.add(new FieldInsnNode(Opcodes.GETSTATIC, "me/guichaguri/tickratechanger/TickrateChanger", "GAME_SPEED", "F"));
                        newCode.add(new InsnNode(Opcodes.FMUL));
                        newCode.add(new VarInsnNode(Opcodes.FSTORE, 2));

                        list.insertBefore(list.getFirst(), newCode)
                    }
                }

                return classNode;
            }
        }
    }
}