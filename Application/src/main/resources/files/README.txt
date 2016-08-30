#
# (c) Smals 2016.
#
# Author: Carlos E. Avimadjessi, Koen Vanderkimpen, Tania Martin
#
# Technical remarks and questions can be addressed to:
# <koen.vanderkimpen@smals.be>
# <tania.martin@smals.be>
# 



+++++ CONTEXT AND PURPOSE +++++

The Belgian eID is a classic smartcard integrating standard public-key cryptography technology. 
One of its functionalities allows its owner to sign digital documents. 
However, if the owner wants to sign 100 documents, he has to type in his PIN code a 100 times as well.

In this context of digital signatures, Smals proposes the signature software SmalsBeSign able to perform batch signing. 
The software generates a digital signature of a set of documents, using the Belgian eID. 
The PIN code is asked only once and used only for such a unique signature. 
This way, the user is in full control of his eID, of his PIN code and of his signature. 
This is in opposition with many other softwares where the user is not in control of the material: the PIN code is usually cached and then used to perform as many signatures as documents to sign.
Note that, even if the software computes one unique signature for a set of documents, it generates an independent signature result (i.e. a zip file) for each document.

The software SmalsBeSign works on Windows (7.0 or higher) and on UNIX-based systems, with internal or external Belgian eID card readers and appropriate middleware already installed on the computer.
It can be downloaded at www.smalsresearch.be/tools/smalsbesign/ 



+++++ SIGN DOCUMENTS +++++


The signature software SmalsBeSign can be used to sign documents with the Belgian eID card.


** Procedure **

- Download and install the software by following the instructions provided on the webpage www.smalsresearch.be/tools/smalsbesign/
- Open the software "SmalsBeSign".
- Click on "Sign files".
- Select the documents to sign with "Select files". 
  You can preview PDF documents on the right part. 
  Other types of documents can be opened with their default software (e.g. Microsoft Word or PowerPoint).
- Put the eID card inside the card reader. On the bottom bar, it then indicates "Ready to sign!".
- Click on "Sign files".
- Enter the eID PIN code.
- Choose the folder to store the result files.
- For each document that has been signed, the result file is a zip file (named name_of_the_document.signed.zip) containing:
	- this README,
	- the original document,
	- the signature file in XML format.


** Result **

The software outputs a message saying that the zip file(s) has(have) been correctly saved.
Since the software checks if the chain certificates is correct, it may output an ORANGE/WARNING when it could not verify one or several problematic certificates because
	- either there was no internet connexion allowing to check the validity and revocation of certificates (e.g. proxy used),
	- or the problematic certificate(s) has(have) already been revoked.



+++++ VERIFY A SIGNATURE +++++


The signature software SmalsBeSign can be used to verify the signature of a document that has been performed with the Belgian eID card and the software itself (see previous section "SIGN DOCUMENTS").


** Procedure **

- Download and install the software by following the instructions provided on the webpage www.smalsresearch.be/tools/smalsbesign/
- Open the software "SmalsBeSign".
- Click on "Verify signed files".
- Select the zip files (only with .signed.zip extension) to verify with "Select zip files". 
  You can preview PDF documents on the right part. 
  Other types of documents can be opened with their default software (e.g. Microsoft Word or PowerPoint).
- Click on "Verify signed files". The result is simply outputted on the screen.


** Result **

Depending on the success of the verifications performed by the software, several outputs are possible.
- GREEN/OK: Every verification is OK.
- ORANGE/WARNING: During the chain certificate verification, the software could not verify one or several problematic certificates because
	- either there was no internet connexion allowing to check the validity and revocation of certificates (e.g. proxy used),
	- or the problematic certificate(s) has(have) already been revoked.
- RED/FAILED: Either the master digest, or the chain certificate, or the signature is not correct.



+++++ CRYPTOGRAPHIC EXPLANATION (ADVANCED) +++++


This section aims to explain in details the cryptographic computation and verification of the signature.


** Computation of the signature (in details) **

Let's imagine that a user wants to sign 3 documents D1, D2 and D3.
In order to do so, the user first gives these documents to the software.
The software then computes the hash of each document, respectively H1, H2 and H3. 
The hash algorithm used at this step is SHA-256.
Then, it concatenates all the hashes. The resulting value H1||H2||H3 is called the "master digest".
The software then verifies that the certificate chain of trust of the plugged eID is correct, including, when there is an internet connection, whether the certificates are revoked or not on the official webpage http://repository.eid.belgium.be/ .
It then asks the user eID to compute the signature of the master digest with the user private key. 
The signature algorithm used at this step is SHA1withRSA (OID = 1.2.840.113549.1.1.5).
Finally the software outputs a zip file for each signed document that contains the following:
- this README,
- the document (D1, D2 or D3 in our example),
- the signature file in XML format that contains
	- the user whose eID has been used to compute the signature (tag <SignedBy>),
	- the system date when the signature has been computed (tag <SignedAt>),
	- the master digest (tag <MasterDigest>),
	- the signature (tag <Signature>),
	- the 3 certificates that form the certificate chain of trust of the user certificate: the user certificate (tag <User>), the intermediate certificate (tag <Intermediate>), and the root certificate (tag <Root>).


** Verification of the signature (in details) **

Let's imagine that a user wants to verify a zip file containing a document D and a signature file.
First, the software hashes the document D contained in the given zip file. It thus obtains the value H.
Then, it verifies that this hash value H is in the master digest (extracted from the signature file).
If not, it means that the information contained in the signature file does not correspond to the document. 
Thus the verification fails and stops.
If H is found in the master digest, then the software checks two points. 
- First, it verifies that the certificate chain of trust is correct, including, when there is an internet connection, whether the certificates are revoked or not on the official webpage http://repository.eid.belgium.be/ . 
- Then it verifies if the signature of the master digest is valid with the public key of the user (this key is stored in the user certificate field of the signature file). 