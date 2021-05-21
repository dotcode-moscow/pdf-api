# Extract text from a PDF (pdf to text). API in docker.

**Why did we create this project?**
1. In the `Laravel` project, it was necessary to extract texts from large files. Existing packages do not work with files larger than 50 megabytes.
2. Text extraction is an expensive operation. Running on a separate server will reduce the load.
3. It was necessary to create a cover for the source.

## Installation
[Install Docker and Docker Compose](https://docs.docker.com/compose/install/)
```sh
git clone https://github.com/dotcode-moscow/pdf-api.git
cd pdf-api
docker-compose up -d pdf-api
```

## Method /api/extractText
Extracts text from a file. As a parameter, we pass the URL to the file.
## Method /api/pdf/ping
ping-pong method
## Method /api/imageToPDF
Image to pdf converter

## Basic example
```sh
curl -d "url=https://trove.nla.gov.au/newspaper/rendition/nla.news-page29291123.pdf" "http://localhost:8080/api/extractText"
```
## POST(HTTP) example
```sh
http://localhost:8080/api/extractText?url=https://trove.nla.gov.au/newspaper/rendition/nla.news-page29291123.pdf
```
## Response (JSON) example
"Page number" (without sorting) and "extracted text".<br>
"img" - jpeg base64 front page cover
```json
{
  "1":"National Library of Australia...",
  "img": "data:image/jpeg;base64..."
}
```

## Production mode
```sh
network_mode: "host"
```

## Credit
[PDFBox](https://pdfbox.apache.org/)

## Contributing
Pull requests are welcome.
