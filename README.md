# TCSS487 - Cryptography Project

This the project report for Joshua Barbee and James Deal.

This project implements KMACXOF256 for use of:
* Computing a cryptographic hash of a given file or console input
* Computing an authentication tag (MAC) of a given file or console input with a passphrase from the console.
* Encrypting a given file symmetrically with a given passphrase.
* Decrypting a given symmetric cryptogram with a given passphrase.

It also uses DHIES/Schnorr Signatures and Elliptic Curves using Ed448 Goldilocks Points for use of:
* Computing Public/Private key pairs from passphrases, writing each key (with the private key encrypted using the included KMACXOF256 functionality with the same given passphrase) to its own file.
* Asymmetrically encrypting a file or console input using a specified public key file and writing the encrypted contents to a file.
* Decrypting a file generated with this application, using the provided passphrase (so that the original private key can be calculated).
* Signing a file or console input using a given passphrase and writing the signature to file.
* Verifying a signature file and source file using a given public key generated with this application.

The details of each function and how to use this project are given below.

## How to use
This is a Java project and is presented as .java files, not compiled. The main method to start the program from exists in the Main class, and there are no other main methods present in the project. To run, either compile the project manually or use an IDE such as IntelliJ and run the main method present in Main. Interaction with this program is done completely through the console.
### Navigation
When run, the program provides a main menu for the suite of options available. The main menu offers each functionality, as well as the ability to run the provided tests for the cryptographic machinery. To choose a menu option, type the number associated with the function and press enter. The option to quit is also available from the main menu. Some features have sub menus, such as for computing authentication tags. In these cases there is also an option for returning to the main menu.

&nbsp;
### Computing a plain cryptographic hash
From the main menu, select the cryptographic hash option, and then select the option for using either an existing file or text typed in the console as the input data. Enter the path of the input file or the immediate text input as directed. The resulting hash value will be printed to the console.
### Computing an authentication tag (MAC) with a given passphrase
From the main menu, select the MAC option, and then select the option for using either an existing file or text typed in the console as the input data. Enter the path of the input file or the immediate text input as directed. Next enter a passphrase under which to compute the authentication tag. The resulting MAC will be printed to the console.
### Encrypting a data file symmetrically with a given passphrase
From the main menu, select the symmetric encryption option. Enter the path of the input file and then the immediate text of the passphrase as directed. Enter a name — possibly including a full filepath — for the cryptogram (a name not previously used for encryption, or the existing cryptogram with that name may be overwritten); this name will be used later for decryption.
### Decrypting a symmetric cryptogram with a given passphrase
From the main menu, select the decryption option. Enter the exact same name that was provided for a cryptogram generated with the application's encryption mode. Enter the exact same passphrase provided when the cryptogram was created, and enter the path of the output file to be created. A message will be printed to the console to indicate whether the provided passphrase is correct for the specified cryptogram, and the decrypted data will be stored in the specified output file if the passphrase is correct.
### Creating an Elliptic Key Pair for asymmetric services with a given passphrase
From the main menu, select the option to generate a Schnorr/DHIES key pair. Enter a desired passphrase and output filename, not including a file extension. The public key will be written to a file named equal to what was desired, with a .bin extension. The private key will be symmetrically encrypted using the included KMACXOF256 functionality and saved to a separate .bin file (from the provided output file name with "_private" &mdash; without the quotes &mdash; appended).
### Encrypting data asymmetrically using Schnorr/DHIES
From the main menu, select the option to encrypt asymmetrically using Schnorr/DHIES. There is a sub-menu for deciding if the content to encrypt will come from a file or console input. The application will ask the user what the output file will be named, not including an extension as this will always be .bin. If the source data (requested from the user next) is a file, the filename given must include the file extension. The subsequently typed location of the public key file should be given without a file extension as it is expected it will always be a .bin. The encrypted data will be saved to a .bin file with a name typed by the user.
### Decrypting an asymmetric cryptogram using Schnorr/DHIES
From the main menu, select the option to decrypt asymmetrically using Schnorr/DHIES. The output filename should include the desired extension. The source file containing the cryptogram (requested next) must be a .bin file generated by this application, and the .bin is inferred and should not be typed in. Finally, enter the passphrase needed to generate the private key necessary for decryption.
### Generate signature using Schnorr/DHIES
From the main menu, select the option to generate a signature using Schnorr/DHIES. There is a sub-menu for deciding whether the content to be signed will come from a file or console input. The passphrase to use when signing and a name for the resulting signature file must then be provided. A file extension is not required for the signature file as it will always be .bin. The source file with a file extension or the immediate text input to sign must then be provided. If immediate text input is provided, an additional filename will be requested to store the original text input, allowing for the signature to be easily verified using this application's verification service.
### Verify signature using Schnorr/DHIES
From the main menu, select the option to verify a signature using Schnorr/DHIES. This function will only verify files with signatures generated by this application. The location of the file to verify a signature for (the original data that was signed) must be provided with a file extension. When subsequently providing the locations of the required signature and public key files, extensions are not required as it is inferred that they will always be .bin files.

&nbsp;
### File input and output
File input is done by typing the name of the file (relative to the execution directory, or as a fully qualified path) into the console when prompted.
### Example usage
User input is highlighted in green, program output is in black. The small "FF" character present is  a location where the console would clear if ran from a proper terminal instead of through an IDE's environment.

Computing a cryptographic hash from console input:

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954409586311168/image.png" alt="hashing">

Symmetrically encrypting a file named "vorspeech.txt" with passphrase "weather", to output named "encod". The actual names of these output files generated will be encod_c.bin, encod_t.bin, and encod_z.bin:

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954409825378446/image.png" alt="encrypting">

Symmetrically decrypting the above generated symmetric cryptogram to a file named "decoded.txt":

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954410253189200/image.png" alt="decrypting">

Running the included functionality tests:

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1111847474500485231/5test.png" alt="testing">

Generating Schnorr/DHIES key pair with filename "asynckey.bin" with passphrase "no".

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1111847474747953172/6key.png" alt="key generate">

Encrypting a file named "README.md" as "asynce.bin" using Schnorr/DHIES and the key generated from above functionality.

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1111847475033161728/7enc.png" alt="async encrypt">

Decrypting the "asynce.bin" file generated above as "asyncd" using Schnorr/DHIES and passphrase "no".

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1111847475322572863/8dec.png" alt="async decrypt">

Generate signature file named "asyncs.bin" for file "vs.txt" using Schnorr/DHIES and passphrase "no".

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1111847475586809997/9gsig.png" alt="signature generation">

Verify signature file "asyncs.bin" for file "vs.txt" using Schnorr/DHIES signature file "asyncs.bin" and key file "asynckey.bin".

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1111847475939135535/10vsig.png" alt="signature verification">

## Description of Project Structure
This section describes each piece of functionality within the project listed per individual file present in project, and which group member worked on each given function. Each file's functionality is categorized by whether it is meant for user interaction/utility, or if it is core functionality to the cryptography itself.

Group members are noted by their initials, JB: Joshua Barbee, JD: James Deal.
### Interaction and Utility Functionality
* Main
  * Calls UserInterface and provides no other functionality.
* UserInterface
  * Console interaction with user is defined here, through a suite of methods and variables to handle menu navigation and user input. JD.
  * Printing byte arrays to the console as hex strings in a way which is readable, this functionality is used for user interaction. JD.
  * File interaction is called here, but pairs with the FileIO class for file handling properly. JB,JD.
* FileIO
  * File handling, both reading in from a file to byte arrays, and writing out to a file from byte arrays. JB.
* Util
  * Splitting/Merging byte arrays into new byte arrays. JB.
  * Converting ASCII strings and BigIntegers into byte arrays and bytes. JB.
  * XOR'ing contents of byte arrays. JB.
  * Creating a hex string from a byte array for usage in other parts of the program. JB.
* TestValidity
  * Tests that KMACXOF256 functionality provides correct output via test vectors from resource provided with project documentation. JD.
  * Tests that cSHAKE256 functionality provides correct output via test vectors from resource provided with project documentation. JD.
  * Tests that SHA-3 functionality provides correct output via test vectors from tiny_sha3 resource. JD.
  * Tests that Ed448GoldilocksPoint objects can be generated and manipulated correctly via tests provided with project documentation. JD.
  * Tests that random k values can interact with Ed448GoldilocksPoint objects can be generated and manipulated correctly via tests provided with project documentation. JD.
  * Tests that signing with Schnorr/DHIES with specific values can be done correctly via tests provided by Professor Barreto. JD.
* SymmetricCryptogram
  * Data structure to hold information during encryption and decryption. JB.
* DHIESCryptogram
  * Data structure to hold information during encryption and decryption. JB.
* EllipticKeyPair
  * Data structure to hold information during encryption and decryption. JB.
* SchnorrSignature
  * Data structure to hold information during encryption and decryption. JB.
### Internal Functionality
* Services
  * The suite of methods which UserInterface calls for work to be done. Dials out to KMACXOF256, Ed448GoldilocksPoint, and ModularArithmatic as necessary. JB, JD.
  * Encrypting/Decrypting do calculations for Ke and Ka from here. JB.
* KMACXOF256
  * Takes calls to KMACXOF256 from Services class and enacts internal SHAKE256/cSHAKE256 functionality. JB, JD.
  * Functions are present to assist KMACXOF256 in properly setting up byte arrays. JB.
    * bytepad, leftEncode, rightEncode, encodeString
* ShaObject
  * SHA-3 and SHAKE256 functionality. There is not currently a way for a user to recieve pure SHA-3 output, all properly completed output from this file is SHAKE. JB, JD.
  * The functionality and structure of this file are heavily influenced and based from the tiny_sha3 resource. JB, JD.
  * State is stored in a ByteBuffer to allow for the contents to be called as both byte and long format. JB, JD.
* Keccak
  * The Keccak core round function (Keccak-f) that ShaObject depends on to function. Contains the functionality necessary to enact the Keccak algorithm. JB.
  * Functionality is from resource tiny_sha3.
  * Able to swap the endian of input and output for interaction with ShaObject. JB.
* Ed448GoldilocksPoint
  * Carries state for Ed448 points as well as functionality for manipulating these points. Can compare points for equality and output them as byte arrays for usage in Services. JB.
* ModularArithmetic
  * Includes functionality for the more specific and complicated math operations required for Elliptic Curves using BigIntegers. JB.
  * Has two derived classes, ModP, and ModR, which are used as convenient shorthand in Services for modular arithmetic in these respective moduli. JB.
## Resources
1. [Dr. Markku-Juhani O. Saarinen: tiny_sha3](https://github.com/mjosaarinen/tiny_sha3)
2. [NIST Documentation: cSHAKE256 Test Vectors](https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/cSHAKE_samples.pdf)
3. [NIST Documentation: KMACXOF256 Test Vectors](https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/KMACXOF_samples.pdf)
4. [NIST Special Publication 800-185](https://dx.doi.org/10.6028/NIST.SP.800-185)