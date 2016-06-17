#SFTPSpokesman

An SFTP Server which -when connected to- provides access to Amazon S3 buckets

### Usage

Specify port Start as Java process

```
mvn clean package spring-boot:run
```

then connect

```
sftp -P 21000 -i key.pem localhost
```

### Users & Access

##### Client Access to SFTP Server
 Clients connect to Spokesman SFTP using their ssh key ([testKey](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/keys/testKey.pem) pass is `hubio`).  At the moment, permitted keys are stored in [authorized_keys](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/authorized_keys).  Spokesman will verify it's identity with the key in `authorizedKeysFile` ([application.yml](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/application.yaml))
##### SFTP Access to S3  
 The username provided by the client to Spokesman is then used to limit which Amazon S3 bucket (defined as `home` in [application.yml](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/application.yaml)) is visible to that `user` ([application.yml](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/application.yaml)).
 As directories provided to SFTP users is managed here, Spokesman will connect to S3 as a single user.  Amazon S3 access keys (`s3fs_access_key` & `s3fs_secret_key`) need to be specified in [amazon.properties](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/amazon.properties) for this user in order for Spokesman to access S3.
 On S3, permissions need to be set so that any directory that will be usable by any SFTP user, for example:-
```
 users:
   exampleUsername:
     home: example_bucket
```
  
should be accessable to the S3 user linked to the provided keys, i.e.

```
{
   "Version": "2012-10-17",
   "Statement": [
       {
           "Effect": "Allow",
           "Action": "s3:*",
           "Resource": [
              "arn:aws:s3:::example_bucket/*",
              "arn:aws:s3:::example_bucket"
           ]
       },
       {
           "Effect": "Allow",
           "Action": "s3:ListAllMyBuckets",
           "Resource": "*"
       }
   ]
}
```