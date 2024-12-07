CREATE
DATABASE IF NOT EXISTS club_management;

USE
club_management;

-- 지도교수 테이블
CREATE TABLE professors
(
    prof_id    VARCHAR(20) PRIMARY KEY,
    name       VARCHAR(50)        NOT NULL,
    department VARCHAR(50)        NOT NULL,
    contact    VARCHAR(20) UNIQUE NOT NULL
);

-- 동아리상태 테이블
CREATE TABLE club_status
(
    status_name     VARCHAR(20) PRIMARY KEY,
    is_page_visible BOOLEAN NOT NULL
);

-- 학생 테이블 (동아리 FK는 나중에 추가)
CREATE TABLE students
(
    student_id VARCHAR(20) PRIMARY KEY,
    contact    VARCHAR(20) UNIQUE NOT NULL,
    name       VARCHAR(50)        NOT NULL,
    department VARCHAR(50)        NOT NULL,
    role       VARCHAR(50)        NOT NULL,
    join_date  DATE               NOT NULL,
    club_id    VARCHAR(20)
);

-- 동아리 테이블
CREATE TABLE clubs
(
    club_id        VARCHAR(20) PRIMARY KEY,
    president_id   VARCHAR(20)         NOT NULL,
    prof_id        VARCHAR(20)         NOT NULL,
    status_name    VARCHAR(20)         NOT NULL,
    club_name      VARCHAR(100) UNIQUE NOT NULL,
    activity_field VARCHAR(50)         NOT NULL,
    member_count   INT                 NOT NULL,
    page_url       VARCHAR(200) UNIQUE NOT NULL,
    club_info      TEXT UNIQUE         NOT NULL,
    FOREIGN KEY (president_id) REFERENCES students (student_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (prof_id) REFERENCES professors (prof_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (status_name) REFERENCES club_status (status_name)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- 학생 테이블 생성 (동아리 FK는 나중에 추가)
CREATE TABLE students
(
    id        VARCHAR(20) PRIMARY KEY,
    name      VARCHAR(50) NOT NULL,
    dept      VARCHAR(50) NOT NULL,
    contact   VARCHAR(20) NOT NULL,
    role      VARCHAR(50) NOT NULL,
    join_date DATE        NOT NULL
);

-- 학생 테이블에 FK 추가
ALTER TABLE students
    ADD FOREIGN KEY (club_id) REFERENCES clubs (club_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE;

-- 예산 테이블
CREATE TABLE budgets
(
    receipt_no VARCHAR(20) PRIMARY KEY,
    club_id    VARCHAR(20)    NOT NULL,
    use_date   DATE           NOT NULL,
    use_field  VARCHAR(50)    NOT NULL,
    amount     DECIMAL(10, 2) NOT NULL,
    semester   VARCHAR(20)    NOT NULL,
    FOREIGN KEY (club_id) REFERENCES clubs (club_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 프로젝트 테이블
CREATE TABLE projects
(
    club_id         VARCHAR(20),
    project_name    VARCHAR(100),
    member_count    INT         NOT NULL,
    project_purpose TEXT        NOT NULL,
    project_topic   TEXT        NOT NULL,
    management_tool VARCHAR(50) NOT NULL,
    semester        VARCHAR(20) NOT NULL,
    PRIMARY KEY (club_id, project_name),
    FOREIGN KEY (club_id) REFERENCES clubs (club_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 프로젝트참여 테이블
CREATE TABLE project_participants
(
    student_id   VARCHAR(20),
    club_id      VARCHAR(20),
    project_name VARCHAR(100),
    PRIMARY KEY (student_id, club_id, project_name),
    FOREIGN KEY (student_id) REFERENCES students (student_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (club_id, project_name) REFERENCES projects (club_id, project_name)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 주요활동 테이블
CREATE TABLE main_activities
(
    club_id       VARCHAR(20),
    activity_name VARCHAR(100),
    activity_date DATE    NOT NULL,
    member_count  INT     NOT NULL,
    has_award     BOOLEAN NOT NULL,
    PRIMARY KEY (club_id, activity_name),
    FOREIGN KEY (club_id) REFERENCES clubs (club_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- 주요활동참여 테이블
CREATE TABLE activity_participants
(
    student_id    VARCHAR(20),
    club_id       VARCHAR(20),
    activity_name VARCHAR(100),
    PRIMARY KEY (student_id, club_id, activity_name),
    FOREIGN KEY (student_id) REFERENCES students (student_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (club_id, activity_name) REFERENCES main_activities (club_id, activity_name)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
