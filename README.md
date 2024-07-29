# danfe-simplificada

# EN

## About this project

This project generates a PDF file that represents a Simplified DANFE of an invoice, from the NFe XML.

The layout of this PDF is in accordance with "Technical Note 2020.004" of Brazilian laws.

Feel free to collaborate and use it.

If you modify this project, use the "shadow > shadowJar" task and get the new JAR into "build/libs" directory (more information about this plugin see https://gradleup.com/shadow/introduction/).

## How to use

You can download and compile the project or use the binaries by the steps above:
 - You can download and compile the project or use one of the versions.
 - In the same directory as the JAR, put one or more XML files of the desired NFes.
 - Make sure you are running java 17 or higher.
 - Open a terminal in the directory and run the program: "java -jar danfe-simplificada-x.x.x-all.jar"
 - Wait of PDF file generation.

## Thecnical information

The Libre Barcode 128 font is best suited for printing 44-position numeric barcodes as it supports the Code 128 format, which is more compact and efficient.

You can download this font at: https://fonts.google.com/specimen/Libre+Barcode+128

# PT

## Sobre este projeto

Este projeto gera um arquivo PDF que representa uma DANFE Simplificada de uma nota fiscal, a partir do XML da NFe.

O layout deste PDF está de acordo com a "Nota Técnica 2020.004" das leis brasileiras.

Sinta-se à vontade para colaborar e utilizá-lo.

Se você modificar o projeto, use a task "shadow > shadowJar" e pegue o novo JAR no diretório "build/libs" (mais informações a respeito deste plugin veja https://gradleup.com/shadow/introduction/).

## Como utilizar
 - Você pode baixar e compilar o projeto ou utilizar uma das releses.
 - No mesmo diretório do JAR coloque um ou mais arquivos XML das NFes desejadas.
 - Certifique-se de estar executando com o java 17 ou superior.
 - Abra um terminal no diretório e execute o programa: "java -jar danfe-simplificada-x.x.x-all.jar"
 - Aguarde a geração do arquivo PDF.

## Informações técnicas

A fonte Libre Barcode 128 é mais adequada para a impressão de código de barras com 44 posições, pois suporta o formato Code 128, que é mais compacto e eficiente.

Você pode baixar esta fonte em: https://fonts.google.com/specimen/Libre+Barcode+128
