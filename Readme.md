# Vytvorenie certifikátu

```
openssl genrsa -out private.key 2048
openssl req -key private.key -new -out request.csr
openssl x509 -signkey private.key -in request.csr -req -days 365 -out server.crt
```

# Získanie verejného kľúča z certifikátu a overenie správnosti podpisu

```
openssl x509 -pubkey -noout -in server.crt
openssl dgst -sha256 -verify public.key -signature <(base64 -d ../output-files/data-valid.signed) ../output-files/data-valid.xml
```

# Poznámky

- private.key je zverejnený v repozitári len na ukážku; v reálnej aplikácií by išiel do .gitignore