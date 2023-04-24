# TCSS487 - Cryptography Project

This project implements KMACXOF256 for use of:
* Computing a cryptographic hash of a given file or console input
* Computing an authentication tag (MAC) of a given file or console input with given passphrase.
* Encrypting a given file symmetrically with a given passphrase.
* Decrypying a given symmetric cryptogram with a given passphrase.

The details of each function and how to use this project are given below.

## How to use
This is a Java project and presented as .java files, not compiled. The main method to start the program from exists in the Main class, and there are no other main methods present in the project. To run, either compile the project manually or use an IDE such as IntelliJ and run the main method present in Main. Interaction with this program is done completely through the console.
### Navigation
When run, the program provides a main menu for the suite of options available. The main menu offers each functionality, as well as the ability to run the provided tests for the cryptographic machinery. To choose a menu option, type the number associated with the function and press enter. The option to quit is also available from the main menu as an option. Some features have sub menus, such as for computing authentication tags. In these cases there is also an option for returning to the main menu.
### File input and output
File input is done by typing the name of the file into the console when prompted.

When encrypting a file, three output bin files are generated, each beginning with a name defined by the user, and ending with a unique character.

To decrypt a file, each file must be in the same location as each other, as the user is only prompted for the part of the filename they defined during the encryption process, not the unique ending characters. The program then looks for each file in turn. If successful, decryption generates one output file, with a name defined by the user. The name defined for output must also include the desired filetype, as encryption does not retain this information.
### Example usage
User input is highlighted in green, program output is in black. The small "FF" character present is  a location where the console would clear if ran from a proper terminal instead of through an IDE's environment.

Computing a cryptographic hash from console input:

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954409586311168/image.png" alt="hashing">

Encrypting a file named "vorspeech.txt" with passphrase "weather", to output named "encod". The actual names of these output files generated will be encod_c.bin, encod_t.bin, and encod_z.bin:

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954409825378446/image.png" alt="encrypting">

Decrypting the above generated symmetric cryptogram to a file named "decoded.txt":

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954410253189200/image.png" alt="decrypting">

Running the included functionality tests:

<img src="https://cdn.discordapp.com/attachments/149944536871731200/1099954410488086598/image.png" alt="testing">

## Description of Project Structure
This section describes each piece of functionality within the project listed per individual file present in project, and which group member worked on each given function. Each file's functionality is categorized by if it is meant for user interaction/utility, or if it is core functionality to the cryptography itself.

Group members are noted by their initials, JB: Joshua Barbee, JD: James Deal.
### Interaction and Utility Functionality
* Main
  * Calls UserInterface and provides no other functionality.
* UserInterface
  * Console interaction with user is defined here, through a suite of methods and variables to handle menu navigation and user input. JD.
  * Printing byte arrays to the console as hex strings in a way which is readable, this functionality is used for user interaction. JD.
  * File interaction is called here, but pairs with the FileIO class for file handling properly. JB,JD. 
* FileIO
  * File handling, both reading in from a file to a byte array, and writing out to a file from a byte array. JB.
* Util
  * Splitting/Merging byte arrays into new byte arrays. JB.
  * Converting ASCII strings and BigIntegers into byte arrays and bytes. JB.
  * XOR'ing contents of byte arrays. JB.
  * Creating a hex string from a byte array for usage in other parts of the program. JB.
* TestValidity
  * Tests that KMACXOF256 functionality provides correct output via test vectors from resource provided with project documentation. JD.
  * Tests that cSHAKE256 functionality provides correct output via test vectors from resource provided with project documentation. JD.
  * Tests that SHA-3 functionality provides correct output via test vectors from tiny_sha3 resource. JD.
* SymmetricCryptogram
  * Data structure to hold information during encryption and decryption. JB.
### Internal Functionality
* Services
  * The suite of methods which UserInterface calls for work to be done. Dials out to KMACXOF256 as necessary. JB.
  * Encrypting/Decrypting do calculations for Ke and Ka from here. JB.
* KMACXOF256
  * Takes calls to KMACXOF256 from Services class and enacts internal SHAKE256/cSHAKE256 functionality. JB, JD.
  * Functions are present to assist KMACXOF256 in properly setting up byte arrays. JB.
    * bytepad, leftEncode, rightEncode, encodeString
* ShaObject
  * SHA-3 and SHAKE256 functionality. There is not currently a way for a user to recieve pure SHA-3 output, all properly completed output from this file is SHAKE.
  * The functionality and structure of this file are heavily influenced and based from the tiny_sha3 resource. JB, JD.
  * State is stored in a ByteBuffer to allow for the contents to be called as both byte and long format. JB, JD.
* Keccak
  * The Keccak core that ShaObject depends on to function. Contains the functionality necessary to enact the Keccak algorithm. JB.
  * Able to swap the endian of input and output for interaction with ShaObject. JB.
## Resources
1. [Dr. Markku-Juhani O. Saarinen: tiny_sha3](https://github.com/mjosaarinen/tiny_sha3)
2. [NIST Documentation: cSHAKE256 Test Vectors](https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/cSHAKE_samples.pdf)
3. [NIST Documentation: KMACXOF256 Test Vectors](TODO)
4. [NIST Special Publication 800-185](https://dx.doi.org/10.6028/NIST.SP.800-185)