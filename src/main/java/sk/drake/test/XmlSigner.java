package sk.drake.test;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.util.Base64;

public class XmlSigner {

  public XmlSigner() {
    // use BouncyCastle for cryptographic operations
    Security.addProvider(new BouncyCastleProvider());
  }

  // read PrivateKey from .key file with BouncyCastle's PEMParser
  private PrivateKey readPKCS8PrivateKey(File file) throws IOException {
    try (FileReader keyReader = new FileReader(file)) {
      PEMParser pemParser = new PEMParser(keyReader);
      JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
      PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());
      return converter.getPrivateKey(privateKeyInfo);
    }
  }

  public void signXml(File xmlFile)
      throws GeneralSecurityException, IOException {

    // hash XML file + sign hash with private key (BouncyCastle's SHA256withRSA)
    PrivateKey privateKey = readPKCS8PrivateKey(new File("./certificate/private.key"));
    Signature signature = Signature.getInstance("SHA256withRSA", "BC");
    signature.initSign(privateKey);
    signature.update(Files.readAllBytes(xmlFile.toPath()));
    byte[] signed = signature.sign();

    // get filename without extension
    String signedFilename = xmlFile.getPath();
    signedFilename = signedFilename.substring(0, signedFilename.lastIndexOf("."));

    // save .signed file
    try (FileOutputStream stream = new FileOutputStream(signedFilename + ".signed")) {
      stream.write(Base64.getEncoder().encode(signed));
    }
    System.out.println("XML subor bol podpisany a podpis bol ulozeny do suboru " + signedFilename + ".signed.\n");
  }
}
