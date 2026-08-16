package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ResumeUploadResponse;
import com.example.demo.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    public ResumeService(
            ResumeRepository resumeRepository) {

        this.resumeRepository = resumeRepository;
    }


    // ============================================================
    // UPLOAD + PROCESS RESUME
    // ============================================================

    public ResumeUploadResponse processResume(
            String candidateId,
            MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume file cannot be empty."
            );
        }

        if (!file.getOriginalFilename()
                .toLowerCase()
                .endsWith(".pdf")) {

            throw new IllegalArgumentException(
                    "Only PDF resumes are supported."
            );
        }


        // Extract PDF text

        String text =
                extractText(file);


        // Extract information

        String email =
                extractEmail(text);

        String phone =
                extractPhone(text);

        List<String> skills =
                extractSkills(text);


        // Store resume in Neo4j

        String resumeId =
                resumeRepository.saveResume(
                        candidateId,
                        file.getOriginalFilename(),
                        email,
                        phone,
                        skills
                );


        // Return result

        return new ResumeUploadResponse(
                resumeId,
                candidateId,
                file.getOriginalFilename(),
                email,
                phone,
                skills
        );
    }


    // ============================================================
    // GET RESUMES
    // ============================================================

    public List<Map<String, Object>> getResumes(
            String candidateId) {

        return resumeRepository.getResumes(
                candidateId
        );
    }


    // ============================================================
    // DELETE RESUME
    // ============================================================

    public boolean deleteResume(
            String candidateId,
            String resumeId) {

        return resumeRepository.deleteResume(
                candidateId,
                resumeId
        );
    }


    // ============================================================
    // PDF TEXT EXTRACTION
    // ============================================================

    private String extractText(
            MultipartFile file) throws IOException {

        try (InputStream inputStream =
                     file.getInputStream()) {

            byte[] data =
                    inputStream.readAllBytes();

            try (PDDocument document =
                         Loader.loadPDF(data)) {

                PDFTextStripper stripper =
                        new PDFTextStripper();

                return stripper.getText(
                        document
                );
            }
        }
    }


    // ============================================================
    // EMAIL EXTRACTION
    // ============================================================

    private String extractEmail(
            String text) {

        Pattern pattern =
                Pattern.compile(
                    "[A-Za-z0-9._%+-]+"
                    + "@"
                    + "[A-Za-z0-9.-]+"
                    + "\\.[A-Za-z]{2,}"
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }


    // ============================================================
    // PHONE EXTRACTION
    // ============================================================

    private String extractPhone(
            String text) {

        Pattern pattern =
                Pattern.compile(
                    "(?:\\+91[-\\s]?)?[6-9]\\d{9}"
                );

        Matcher matcher =
                pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group();
        }

        return "";
    }


    // ============================================================
    // SKILL EXTRACTION
    // ============================================================

    private List<String> extractSkills(
            String text) {

        String normalizedText =
                text.toLowerCase();

        Set<String> skills =
                new LinkedHashSet<>();


        String[][] skillDictionary = {

            {"java", "Java"},

            {"spring boot", "Spring Boot"},

            {"spring", "Spring"},

            {"sql", "SQL"},

            {"rest api", "REST API"},

            {"rest", "REST API"},

            {"git", "Git"},

            {"docker", "Docker"},

            {"kafka", "Kafka"},

            {"aws", "AWS"},

            {"react", "React"},

            {"javascript", "JavaScript"},

            {"html", "HTML"},

            {"css", "CSS"},

            {"junit", "JUnit"},

            {"mongodb", "MongoDB"},

            {"mysql", "MySQL"},

            {"oracle", "Oracle"},

            {"hibernate", "Hibernate"},

            {"jpa", "JPA"}
        };


        for (String[] skill :
                skillDictionary) {

            if (normalizedText.contains(
                    skill[0])) {

                skills.add(
                        skill[1]
                );
            }
        }


        return new ArrayList<>(
                skills
        );
    }
}