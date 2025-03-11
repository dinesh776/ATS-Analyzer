# ATS Analyzer

ATS Analyzer is a resume analysis tool that helps users evaluate their resumes against job descriptions (optional) to generate an ATS score, strengths, weaknesses, and actionable recommendations.

## Features
- **Resume Analysis:** Upload a resume to get detailed insights.
- **Job Description Matching:** Optionally provide a job description to assess resume alignment.
- **ATS Score:** Get a score indicating how well your resume fits an ATS.
- **Recommendations:** Receive suggestions for improving your resume.

## Technologies Used
- **Frontend:** HTML, CSS, JavaScript, Bootstrap
- **Backend:** Spring Boot
- **AI Integration:** Spring AI 

## Requirements
To run this application, ensure you have the following:
- **Java 21** (included in the Docker image via OpenJDK 21-slim)
- **Docker Desktop** (if running via Docker)
- **Spring Boot** (managed in the project itself)
- **OpenAI-compatible API credentials** (for AI features)
- **8080 Port Availability** (default app port)

## Getting Started

You can run the application in two ways:

### 1. Clone and Run Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/dinesh776/ATS-Analyzer.git
   cd ATS-Analyzer
   ```

2. Start the backend using Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Open the frontend in your browser.

4. Configure your AI settings:
    - Click on the **Settings** icon.
    - Enter your **Base URL**, **API Key**, **Model Name**, and **Sampling Rate**.
    - Click **Save**.

5. Upload your resume (and optionally a job description) to start the analysis!

### 2. Run with Docker



1. #### Pull and Run Prebuilt Docker Image

   ```bash
   docker pull dineshgvns/ats-analyzer-app
   ```
   
2. #### Alternatively, you can Build and Run the Docker Container 
   1. Build the Docker image:
      ```bash
      docker build -t ats-analyzer .
      ```

   2. Run the Docker container:
      ```bash
      docker run -p 8080:8080 ats-analyzer
      ```

3. Access the application at:
    - **Frontend:** `http://localhost:8080`


## Note
- Only **OpenAI-compatible URLs** are supported at this time.

## Contributing
Feel free to fork the repo, submit issues, or create pull requests.

## License
This project is licensed under the MIT License.

---

Ready to boost your resume? Let ATS Analyzer guide you to success! 🚀

