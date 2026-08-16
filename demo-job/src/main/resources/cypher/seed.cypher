// ===============================
// JOBFLOW SEED DATA
// ===============================

// ---------- SKILLS ----------

CREATE
(java:Skill {id: 'SK001', name: 'Java'}),
(spring:Skill {id: 'SK002', name: 'Spring Boot'}),
(sql:Skill {id: 'SK003', name: 'SQL'}),
(rest:Skill {id: 'SK004', name: 'REST API'}),
(git:Skill {id: 'SK005', name: 'Git'}),
(docker:Skill {id: 'SK006', name: 'Docker'}),
(kafka:Skill {id: 'SK007', name: 'Kafka'}),
(aws:Skill {id: 'SK008', name: 'AWS'}),
(react:Skill {id: 'SK009', name: 'React'}),
(junit:Skill {id: 'SK010', name: 'JUnit'});


// ---------- COMPANIES ----------

CREATE
(techNova:Company {
    id: 'C001',
    name: 'TechNova Solutions',
    location: 'Hyderabad'
}),

(codeWorks:Company {
    id: 'C002',
    name: 'CodeWorks India',
    location: 'Bangalore'
}),

(cloudBase:Company {
    id: 'C003',
    name: 'CloudBase Technologies',
    location: 'Hyderabad'
}),

(finEdge:Company {
    id: 'C004',
    name: 'FinEdge Systems',
    location: 'Pune'
}),

(dataLabs:Company {
    id: 'C005',
    name: 'DataLabs India',
    location: 'Chennai'
});


// ---------- JOBS ----------

CREATE
(job1:Job {
    id: 'J001',
    title: 'Java Backend Developer',
    location: 'Hyderabad',
    salaryMin: 500000,
    salaryMax: 800000,
    experienceMin: 0,
    experienceMax: 2
}),

(job2:Job {
    id: 'J002',
    title: 'Spring Boot Developer',
    location: 'Hyderabad',
    salaryMin: 600000,
    salaryMax: 900000,
    experienceMin: 1,
    experienceMax: 3
}),

(job3:Job {
    id: 'J003',
    title: 'Junior Java Developer',
    location: 'Bangalore',
    salaryMin: 400000,
    salaryMax: 650000,
    experienceMin: 0,
    experienceMax: 2
}),

(job4:Job {
    id: 'J004',
    title: 'Backend Engineer',
    location: 'Hyderabad',
    salaryMin: 700000,
    salaryMax: 1100000,
    experienceMin: 1,
    experienceMax: 3
}),

(job5:Job {
    id: 'J005',
    title: 'Full Stack Developer',
    location: 'Bangalore',
    salaryMin: 500000,
    salaryMax: 900000,
    experienceMin: 0,
    experienceMax: 2
}),

(job6:Job {
    id: 'J006',
    title: 'Cloud Java Developer',
    location: 'Hyderabad',
    salaryMin: 700000,
    salaryMax: 1200000,
    experienceMin: 1,
    experienceMax: 3
});


// ---------- COMPANY -> JOB ----------

CREATE
(techNova)-[:POSTS]->(job1),
(techNova)-[:POSTS]->(job2),
(codeWorks)-[:POSTS]->(job3),
(cloudBase)-[:POSTS]->(job4),
(codeWorks)-[:POSTS]->(job5),
(cloudBase)-[:POSTS]->(job6);


// ---------- JOB -> REQUIRED SKILLS ----------

CREATE
(job1)-[:REQUIRES]->(java),
(job1)-[:REQUIRES]->(spring),
(job1)-[:REQUIRES]->(sql),
(job1)-[:REQUIRES]->(rest),
(job1)-[:REQUIRES]->(git),

(job2)-[:REQUIRES]->(java),
(job2)-[:REQUIRES]->(spring),
(job2)-[:REQUIRES]->(sql),
(job2)-[:REQUIRES]->(rest),
(job2)-[:REQUIRES]->(docker),

(job3)-[:REQUIRES]->(java),
(job3)-[:REQUIRES]->(sql),
(job3)-[:REQUIRES]->(git),

(job4)-[:REQUIRES]->(java),
(job4)-[:REQUIRES]->(spring),
(job4)-[:REQUIRES]->(docker),
(job4)-[:REQUIRES]->(kafka),
(job4)-[:REQUIRES]->(sql),

(job5)-[:REQUIRES]->(java),
(job5)-[:REQUIRES]->(spring),
(job5)-[:REQUIRES]->(react),
(job5)-[:REQUIRES]->(sql),

(job6)-[:REQUIRES]->(java),
(job6)-[:REQUIRES]->(spring),
(job6)-[:REQUIRES]->(aws),
(job6)-[:REQUIRES]->(docker),
(job6)-[:REQUIRES]->(sql);


// ---------- CANDIDATE ----------

CREATE
(candidate:Candidate {
    id: 'U001',
    name: 'Demo Candidate',
    email: 'candidate@example.com',
    phone: '9999999999',
    location: 'Hyderabad',
    experience: 1,
    expectedSalary: 500000
});


// ---------- CANDIDATE SKILLS ----------

CREATE
(candidate)-[:HAS_SKILL]->(java),
(candidate)-[:HAS_SKILL]->(spring),
(candidate)-[:HAS_SKILL]->(sql),
(candidate)-[:HAS_SKILL]->(rest),
(candidate)-[:HAS_SKILL]->(git),
(candidate)-[:HAS_SKILL]->(junit);