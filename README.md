# CryptoProj2Group12016

Compilation Instruction(Windows):javac src/*.java -classpath "./lib/forms_rt.jar;./lib/paillier.jar;./src"
Run Instruction(Windows):java -cp "./lib/forms_rt.jar;./lib/paillier.jar;./src" HumanGui ./voters.txt ./candidates.txt

Assuming that the voter cannot be perfectly impersonated to the ElectionBoard. Assuming that the tail return calls of the decrypted vote represent the election board directly posting results.
Assuming that the private members of a class cannot be accessed by external classes. Assuming 256-bit Paillier encryption is secure(it isn't, but our library doesn't support more).
Assuming that the system has a secure PRNG set.

Using UT Dallas's Pallier library from http://cs.utdallas.edu/dspl/cgi-bin/pailliertoolbox/index.php?go=doc