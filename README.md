S3 Upload Listener (a project of 'My Ipod Customizer Application')

This project will let you listen to an S3 upload event and perform backend services. It is built using AWS Java SDK and leverages AWS Lambda, as micro-services.
On a detailed note, the idea is to extract MP3 file metadata, which are uploaded in S3. Then, the extracted information will be stored in a DynamoDB database.

For the bigger picture, the intention is to be able to update the various metadata of a MP3 file, in a user friendly manner.
This is particularly useful for people having trouble to sort their music files via iTunes. Using the complete set of micro-services and web application associated with the project,
anyone would be able to edit their mp3 file's metadata.

Getting Started

Clone this project and you will be able to start working on it. To run the application as-is, you need to have an AWS account.

Pre-requisities

 1. Create a DynamoDB table named 'MusicInfo' with a hash-key 'uuid' and a range key 'createdTime'
 2. Make sure you have requisite permission to execute Lambda and read data from AWS S3.
 3. Create a S3 bucket called - 'my-music-files'.
 4. Create a folder called 'ipod' inside this new bucket (optional)

Development

	Building

To build s3-upload-listener, you will need:

JDK 8 - Oracle or OpenJDK
maven - Version 3 recommended
After installing these tools simply run 'mvn package shade:shade' and find the jar in the target folder.

Other Useful maven lifecycles:

clean - remove binaries, docs and temporary build files
compile - compile the library
test - run all unit tests
package - package compiled code into a jar

Deployment

	Get the 's3-upload-listener-0.0.1-SNAPSHOT-shaded.jar' and upload it into a Lambda function.
	Give an appropriate name to the Lambda function (like 'Mp3UploadListener')
	Now, go to the 'Event Sources' section of the newly created Lambda function and 'Add event source'.
	Event Source Type: S3
	Bucket: my-music-files (or your choice)
	Event Type: Object Created (All)
	Prefix: ipod/ (or your choice; optional)
	Suffix: .mp3

Built With

    Maven - Maybe

Contributing

I would love to automate the deployment part, using CloudFormation perhaps? If anyone can provide me a guide, I will be much obliged.

If you have added a feature or fixed a bug in 's3-upload-listener' please submit a pull request as follows:

Fork the project
Write the code for your feature or bug fix
Please don't auto-format the code or make wholesale whitespace changes as it makes seeing what has changed more difficult
Add tests! This is important so the code you've added doesn't get unintentionally broken in the future
Make sure the existing tests pass
Commit and do not mess with version or history
Submit a pull request
Thanks for sharing!


Authors

    Aritra Nayak - Initial work

License

This project is licensed under the MIT License - see the LICENSE.md file for details

Acknowledgments

    mpatric for his mp3agic project
