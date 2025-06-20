#/bin/bash
JAVA_HOME=/mnt/programas/jdk-17.0.13_x64
PATH=$JAVA_HOME/bin:$PATH
java -jar danfe-simplificada-0.6.0-all.jar --layout-break=2

# PARÂMETROS:
#--layout-break=2
# Geração das DANFEs: 0-Em sequência, 1-Duas por folha (lado a lado), 2-Uma por página
#--show-customer-phone=0
# Oculta telefone do cliente
#--show-invoice-total=1
# Mostra valor total da nota
