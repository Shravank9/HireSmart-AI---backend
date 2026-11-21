-- Default Seed Data for HireSmart AI (MySQL & Workbench compatible)
INSERT IGNORE INTO users (id, email, password_hash, full_name, role, is_verified, status) VALUES
(1, 'admin@hiresmart.ai', '$2a$10$eAccYoNOz2F5DO7edAOAOO89.v9Hq1fK3hWJ9Y8u0N5v0O9v5N4a2', 'HireSmart SuperAdmin', 'ADMIN', TRUE, 'ACTIVE'),
(2, 'recruiter@techcorp.com', '$2a$10$eAccYoNOz2F5DO7edAOAOO89.v9Hq1fK3hWJ9Y8u0N5v0O9v5N4a2', 'Priya Sharma', 'RECRUITER', TRUE, 'ACTIVE');

INSERT IGNORE INTO companies (id, name, website, description, industry, location) VALUES
(1, 'TechCorp Innovations', 'https://techcorp.example.com', 'Leading cloud and AI software enterprise solutions provider.', 'Information Technology', 'Bengaluru, Karnataka');

INSERT IGNORE INTO recruiters (id, user_id, company_id, department, title, phone) VALUES
(1, 2, 1, 'Talent Acquisition', 'Lead Technical Recruiter', '+91 98765 43210');

INSERT IGNORE INTO jobs (id, company_id, recruiter_id, title, department, description, location, employment_type, experience_level, min_salary, max_salary, currency, required_skills, status, approval_status, vacancies_count) VALUES
(1, 1, 1, 'Senior Java 25 Spring Boot Engineer', 'Core Backend', 'We are seeking a seasoned Java backend engineer to lead our microservices transformation using modern Java 25 runtime. You will architect robust, fault-tolerant REST APIs, design relational data models, and integrate AI agent features.', 'Bengaluru, Karnataka / Remote', 'FULL_TIME', 'Senior (4+ years)', 1600000, 2400000, 'INR', '["Java 25", "Spring Boot", "Spring Security", "MySQL", "Docker", "REST APIs", "Microservices"]', 'ACTIVE', 'APPROVED', 4),
(2, 1, 1, 'Full-Stack React & Spring Developer', 'Product Engineering', 'Seeking a dual-threat full stack developer to ship candidate-facing analytics dashboards and recruitment workflows using React, Tailwind CSS, Java 25, and Spring Boot.', 'Hyderabad, Telangana / Hybrid', 'FULL_TIME', 'Mid-Level (2-4 years)', 1200000, 1800000, 'INR', '["React.js", "JavaScript", "Tailwind CSS", "Java 25", "Spring Boot", "REST APIs"]', 'ACTIVE', 'APPROVED', 2),
(3, 1, 1, 'AI & NLP Integration Engineer', 'AI Research', 'Join our pioneering AI team to develop production generative AI agents, automated resume parsing pipelines, and speech-to-text interview analytics engines.', 'Bengaluru / Remote', 'FULL_TIME', 'Senior (3+ years)', 1800000, 2800000, 'INR', '["Python", "FastAPI", "Spring Boot", "LLMs", "Vector Databases", "PyTorch"]', 'ACTIVE', 'APPROVED', 3),
(4, 1, 1, 'DevOps & Cloud Infrastructure Specialist', 'Cloud Infrastructure', 'Lead our Kubernetes and Cloudflare cloud migrations. Manage Docker containers, Helm charts, and automated zero-downtime CI/CD release pipelines.', 'Pune / Remote', 'FULL_TIME', 'Mid-Senior', 1400000, 2000000, 'INR', '["Kubernetes", "Docker", "AWS", "CI/CD", "Linux"]', 'ACTIVE', 'APPROVED', 2);
