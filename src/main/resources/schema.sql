-- Schema initialization for H2 / MySQL
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(191) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    avatar_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS companies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    website VARCHAR(255),
    description TEXT,
    logo_url VARCHAR(500),
    industry VARCHAR(100),
    location VARCHAR(150),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    phone VARCHAR(30),
    title VARCHAR(150),
    location VARCHAR(150),
    bio TEXT,
    education_json TEXT,
    skills_json TEXT,
    experience_json TEXT,
    projects_json TEXT,
    certifications_json TEXT,
    linkedin_url VARCHAR(255),
    github_url VARCHAR(255),
    portfolio_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS recruiters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    company_id BIGINT NOT NULL,
    department VARCHAR(100),
    title VARCHAR(100) DEFAULT 'Technical Recruiter',
    phone VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_id BIGINT NOT NULL,
    recruiter_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    department VARCHAR(100),
    description TEXT NOT NULL,
    location VARCHAR(150) NOT NULL,
    employment_type VARCHAR(50) DEFAULT 'FULL_TIME',
    experience_level VARCHAR(50) DEFAULT 'Mid-Senior',
    min_salary DECIMAL(12, 2),
    max_salary DECIMAL(12, 2),
    currency VARCHAR(10) DEFAULT 'INR',
    required_skills TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'ACTIVE',
    approval_status VARCHAR(50) DEFAULT 'APPROVED',
    vacancies_count INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resumes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    candidate_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    storage_url VARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(100) DEFAULT 'application/pdf',
    extracted_text TEXT NOT NULL,
    upload_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resume_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resume_id BIGINT NOT NULL UNIQUE,
    summary TEXT,
    technical_skills TEXT,
    soft_skills TEXT,
    education_summary TEXT,
    experience_summary TEXT,
    strengths TEXT,
    gaps_missing_info TEXT,
    suggested_improvements TEXT,
    potential_job_roles TEXT,
    raw_ai_response TEXT,
    analyzed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    status VARCHAR(50) DEFAULT 'APPLIED',
    rejection_reason VARCHAR(500),
    match_score DECIMAL(5, 2) DEFAULT 0.00,
    skills_match_score DECIMAL(5, 2) DEFAULT 0.00,
    experience_match_score DECIMAL(5, 2) DEFAULT 0.00,
    education_match_score DECIMAL(5, 2) DEFAULT 0.00,
    match_explanation TEXT,
    matched_skills TEXT,
    missing_skills TEXT,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS email_verifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(191) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    attempts_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS interviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    recruiter_id BIGINT,
    candidate_id BIGINT NOT NULL,
    scheduled_time TIMESTAMP NOT NULL,
    meeting_link VARCHAR(500),
    interview_type VARCHAR(50) DEFAULT 'AI_MOCK',
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS interview_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT,
    interview_id BIGINT,
    question_text TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'TECHNICAL',
    difficulty VARCHAR(50) DEFAULT 'MEDIUM',
    sample_answer TEXT,
    order_index INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS interview_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    video_storage_url VARCHAR(1000),
    audio_storage_url VARCHAR(1000),
    transcript_text TEXT,
    score DECIMAL(5, 2) DEFAULT 0.00,
    ai_feedback TEXT,
    answered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS interview_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL UNIQUE,
    candidate_id BIGINT NOT NULL,
    technical_score DECIMAL(5, 2) NOT NULL,
    clarity_score DECIMAL(5, 2) NOT NULL,
    relevance_score DECIMAL(5, 2) NOT NULL,
    overall_score DECIMAL(5, 2) NOT NULL,
    strengths TEXT,
    areas_for_improvement TEXT,
    question_feedback_json TEXT,
    summary TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS application_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    application_id BIGINT NOT NULL,
    previous_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    changed_by_user_id BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    company_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    ip_address VARCHAR(45),
    details TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
