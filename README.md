#SFTPSpokesman

An SFTP Server which -when connected to- provides access to Amazon S3 buckets

### Usage

Start as Java process

```
mvn clean package spring-boot:run -Dsftp.port=21000 -Dsftp.pubkey.file=key.pem
```

then connect

```
sftp -P 21000 -i key.pem localhost
```