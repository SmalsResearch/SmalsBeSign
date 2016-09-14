#
# SmalsBeSign
#
#
# Copyright (C) 2016 SmalsResearch.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Lesser General Public License version 3
# as published by the Free Software Foundation.
#
# As allowed for by provision 7 "Additional Terms" of aforementions
# License, you are required to identify "SmalsResearch" as the author
# of the Program by clearly including this name in all relevant notices
# attached to or part of any Covered Work, as defined in provision 0
# "Definitions".
#
# This program is distributed in the hope that it will be useful, but
# WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
# License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
#
# This program is a combined work that uses the following libraries:
# - Commons eID Client (under GNU Lesser GPL),
# - FontAwesomeFx (under Apache 2.0 License),
# - OpenViewerFx (under GNU Lesser GPL),
# - JFoenix (under Apache 2.0 License),
# - BouncyCastle (under MIT License).
#
#
# Developers: Carlos E. Avimadjessi, Koen Vanderkimpen, Tania Martin
#
# Technical remarks and questions can be addressed to:
# <koen.vanderkimpen@smals.be>
# <tania.martin@smals.be>
#




+++++ CONTEXT AND PURPOSE +++++

The Belgian eID is a classic smartcard integrating standard public-key
cryptography technology. One of its functionalities allows its owner to
sign digital documents. However, if the owner wants to sign 100 documents,
either he has to type in his PIN code a 100 times as well, or his PIN code
is cached and then used to perform as many signatures as documents to sign.

In this context of digital signatures, SmalsResearch proposes the signature
software SmalsBeSign able to perform batch signing. The software generates
a digital signature of a set of documents, using the Belgian eID. The PIN
code is asked only once and used only for such a unique signature. This way,
the user is in full control of his eID, of his PIN code and of his signature.
This is in opposition with many other softwares where the user is not in
control of the material (e.g. when the PIN code is cached). Note that, even
if the software computes one unique signature for a set of documents, it
generates an independent signature result (i.e. a zip file) for each document.

The software SmalsBeSign works on Windows (7.0 or higher) and on UNIX-based
systems, with internal or external Belgian eID card readers and appropriate
middleware already installed on the computer.
It requires Java 1.8.
It can be downloaded at www.smalsresearch.be/tools/smalsbesign/



+++++ SIGN DOCUMENTS +++++


The signature software SmalsBeSign can be used to sign documents with the
Belgian eID card.


** Procedure **

 1. Download and install the software by following the instructions provided
    on the webpage www.smalsresearch.be/tools/smalsbesign/
 2. Open/run the software "SmalsBeSign".
 3. If you are behind a proxy, click on "File > Settings", then fill the proxy
    settings and check "Use proxy".
 4. Click on "Sign files" or "Task > Sign".
 5. Select the documents to sign with "Select files".
    You can preview PDF documents on the right part.
    Other types of documents can be opened with their default software (e.g.
    Microsoft Word or PowerPoint).
 6. Connect a card reader and put the eID card inside the card reader.
    On the bottom bar, it then indicates "Ready to sign!".
 7. Click on "Sign files".
 8. Enter the eID PIN code.
    BE CAREFUL: You have only 3 attempts to enter the correct PIN code,
    otherwise the eID card gets automatically blocked (for security reasons).
 9. Choose the folder to store the result files.
10. For each document that has been signed, the result file is a zip file
    (named name_of_the_document.signed.zip) containing:
	- this README,
	- the original document,
	- the signature file in XML format.


** Result **

The software outputs a message saying that the zip file(s) has(have) been
correctly saved.

Since the software checks if the chain of certificates is correct, it may
output an ORANGE/WARNING when it could not verify one or several problematic
certificates because
	- either there was no internet connexion allowing to check the validity
	  and revocation of certificates (e.g. no connection, or behind a proxy
	  that hasn't been or could not be configured in "File > Settings"),
	- or the problematic certificate(s) has(have) already been revoked.



+++++ VERIFY A SIGNATURE +++++


The signature software SmalsBeSign can be used to verify the signature of a
document that has been performed with the Belgian eID card and the software
itself (see previous section "SIGN DOCUMENTS").


** Procedure **

 1. Download and install the software by following the instructions provided
    on the webpage www.smalsresearch.be/tools/smalsbesign/
 2. Open/run the software "SmalsBeSign".
 3. If you are behind a proxy, click on "File > Settings", then fill the proxy
    settings and check "Use proxy".
 4. Click on "Verify signed files" or "Task > Verify".
 5. Select the zip files to verify with "Select zip files".
    You can preview PDF documents on the right part.
    Other types of documents can be opened with their default software (e.g.
    Microsoft Word or PowerPoint).
 6. Click on "Verify signed files". The result is simply outputted on the screen.


** Result **

Depending on the success of the verifications performed by the software,
several outputs are possible.
- GREEN/OK: Every verification is OK.
- ORANGE/WARNING: During the chain certificate verification, the software
  could not verify one or several problematic certificates because
	- either there was no internet connexion allowing to check the validity
	  and revocation of certificates (e.g. no connection, or behind a proxy
	  that hasn't been or could not be configured in "File > Settings"),
	- or the problematic certificate(s) has(have) already been revoked.
- RED/FAILED: Either the master digest, or the chain certificate, or the
  signature is not correct.



+++++ CRYPTOGRAPHIC EXPLANATION (ADVANCED) +++++


This section aims to explain in details the cryptographic computation and
verification of the signature.


** Computation of the signature (in details) **

Let's imagine that a user wants to sign 3 documents D1, D2 and D3. In order
to do so, the software first verifies that the certificate chain of trust of
the plugged eID is correct, including, when there is an internet connection,
whether the certificates are revoked or not on the official webpage:
http://repository.eid.belgium.be/

Then the user provides the three documents to the SmalsBeSign. The software
then computes the hash of each document, respectively H1, H2 and H3. The hash
algorithm used at this step is SHA-256. Then, SmalsBeSign concatenates all
the  hashes. The resulting value H1||H2||H3 is called the "master digest".

SmalsBeSign then asks the user eID to compute the signature of the master
digest with the user private key. The signature algorithm used at this step
is SHA1withRSA (OID = 1.2.840.113549.1.1.5).

Finally, SmalsBeSign outputs a zip file for each signed document that contains
the following:
- this README file explaining how to use the software,
- the document (either D1, D2 or D3 in our example),
- the signature file in XML format that contains
	- the version number of the software SmalsBeSign used to compute the
	  signature (tag <SoftwareVersion>),
	- the user whose eID has been used to compute the signature (tag <SignedBy>),
	- the system date when the signature has been computed (tag <SignedAt>),
	- the master digest (tag <MasterDigest>),
	- the signature (tag <Signature>),
	- the 3 certificates that form the certificate chain of trust of the
	  user certificate: the user certificate (tag <User>), the intermediate
	  certificate (tag <Intermediate>), and the root certificate (tag <Root>).


** Verification of the signature (in details) **

Let's imagine that a user wants to verify a zip file containing a document
D2 and a signature file. First, SmalsBeSign hashes this document D2 contained
in the given zip file. It then verifies that this hash value is inside the
master digest (extracted from the signature file). If not, it means that the
information contained in the signature file does not correspond to the document
D2. Thus the verification fails and stops.

If the hash value is found in the master digest, then SmalsBeSign checks two
more things. First, it verifies if the certificate chain of trust contained
in the signature file is correct, including, if there is an internet connection,
whether the certificates are revoked or not on the official webpage
http://repository.eid.belgium.be/.
Then, it verifies if the signature of the master digest is valid with the public
key of the user that supposedly performed the signature (this key is stored
in the user certificate field of the signature file).