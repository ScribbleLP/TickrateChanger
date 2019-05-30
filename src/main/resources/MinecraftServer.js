function initializeCoremod() {
    return {
        'coremodone': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.ChunkRenderContainer'
            },
            'transformer': function(classNode) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes')

                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode')
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode')
                var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode')
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode')

                var api = Java.type('net.minecraftforge.coremod.api.ASMAPI');

                var methods = classNode.methods;

                for(m in methods) {
                    var method = methods[m];
                    if (method.name.equals("run")) {
                        var list = method.instructions;
                        var code = list.toArray();

                        for (var i = 0; i < code.length; i++) {
                            var insn = code[i];

                            if (insn instanceof LdcInsnNode) {
                                if (insn.cst === 50) {
                                    list.insertBefore(insn, new FieldInsnNode(Opcodes.GETSTATIC, "me/guichaguri/tickratechanger/TickrateChanger", "MILISECONDS_PER_TICK", "J"))
                                    list.remove(insn)
                                }
                            }
                        }
                    }
                }

                return classNode;
            }
        }
    }
}