package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.ResumeUploadResponse;
import com.example.demo.service.ResumeService;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(
            ResumeService resumeService) {

        this.resumeService = resumeService;
    }


    // ============================================================
    // UPLOAD RESUME
    // ============================================================

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResumeUploadResponse uploadResume(

            @RequestParam("candidateId")
            String candidateId,

            @RequestParam("file")
            MultipartFile file)

            throws Exception {

        return resumeService.processResume(
                candidateId,
                file
        );
    }


    // ============================================================
    // GET ALL RESUMES
    // ============================================================

    @GetMapping("/{candidateId}")
    public List<Map<String, Object>> getResumes(

            @PathVariable String candidateId) {

        return resumeService.getResumes(
                candidateId
        );
    }


    // ============================================================
    // DELETE RESUME
    // ============================================================

    @DeleteMapping("/{candidateId}/{resumeId}")
    public ResponseEntity<?> deleteResume(

            @PathVariable String candidateId,

            @PathVariable String resumeId) {

        boolean deleted =
                resumeService.deleteResume(
                        candidateId,
                        resumeId
                );

        if (!deleted) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(
                Map.of(
                    "message",
                    "Resume deleted successfully",
                    "resumeId",
                    resumeId
                )
        );
    }
}