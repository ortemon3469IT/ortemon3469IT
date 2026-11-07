-- Extensions
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
-- Sequences & Tables
CREATE SEQUENCE public.users_user_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.users (
    user_id integer DEFAULT nextval('public.users_user_id_seq'::regclass) NOT NULL,
    nickname character varying(50) NOT NULL,
    user_role character varying(20) DEFAULT 'student'::character varying,
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT users_login_key UNIQUE (nickname),
    CONSTRAINT users_role_check CHECK (
        (
            (user_role)::text = ANY (
                (
                    ARRAY ['student'::character varying, 'teacher'::character varying, 'admin'::character varying]
                )::text []
            )
        )
    )
);
CREATE SEQUENCE public.titles_title_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.titles (
    title_id integer DEFAULT nextval('public.titles_title_id_seq'::regclass) NOT NULL,
    title_name character varying(50) NOT NULL,
    min_level integer NOT NULL,
    CONSTRAINT titles_pkey PRIMARY KEY (title_id),
    CONSTRAINT titles_title_name_key UNIQUE (title_name),
    CONSTRAINT titles_min_level_check CHECK ((min_level >= 1))
);
CREATE TABLE public.user_profiles (
    user_id integer NOT NULL,
    xp integer DEFAULT 0,
    current_level integer DEFAULT 1,
    coins integer DEFAULT 0,
    current_streak integer DEFAULT 0,
    title_id integer,
    CONSTRAINT user_profiles_pkey PRIMARY KEY (user_id),
    CONSTRAINT user_profiles_coins_check CHECK ((coins >= 0)),
    CONSTRAINT user_profiles_current_level_check CHECK ((current_level >= 1)),
    CONSTRAINT user_profiles_current_streak_check CHECK ((current_streak >= 0)),
    CONSTRAINT user_profiles_xp_check CHECK ((xp >= 0)),
    CONSTRAINT user_profiles_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    CONSTRAINT user_profiles_title_id_fkey FOREIGN KEY (title_id) REFERENCES public.titles(title_id) ON DELETE
    SET NULL
);
CREATE SEQUENCE public.topics_topic_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.topics (
    topic_id integer DEFAULT nextval('public.topics_topic_id_seq'::regclass) NOT NULL,
    topic_name character varying(100) NOT NULL,
    description text,
    CONSTRAINT topics_pkey PRIMARY KEY (topic_id),
    CONSTRAINT topics_topic_name_key UNIQUE (topic_name)
);
CREATE SEQUENCE public.tasks_task_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.tasks (
    task_id integer DEFAULT nextval('public.tasks_task_id_seq'::regclass) NOT NULL,
    topic_id integer,
    task_text text NOT NULL,
    correct_answer character varying(255) NOT NULL,
    complexity integer NOT NULL,
    start_date timestamp without time zone DEFAULT now(),
    deadline timestamp without time zone,
    CONSTRAINT tasks_pkey PRIMARY KEY (task_id),
    CONSTRAINT tasks_topic_id_fkey FOREIGN KEY (topic_id) REFERENCES public.topics(topic_id) ON DELETE
    SET NULL
);
-- Partitioned Table: user_solutions
CREATE TABLE public.user_solutions (
    solution_id integer GENERATED ALWAYS AS IDENTITY,
    user_id integer,
    task_id integer,
    grade_level integer NOT NULL,
    is_correct boolean NOT NULL,
    submitted_at timestamp without time zone DEFAULT now(),
    CONSTRAINT user_solutions_pkey PRIMARY KEY (solution_id, grade_level),
    CONSTRAINT user_solutions_grade_level_check CHECK (
        (
            (grade_level >= 5)
            AND (grade_level <= 11)
        )
    ),
    CONSTRAINT user_solutions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    CONSTRAINT user_solutions_task_id_fkey FOREIGN KEY (task_id) REFERENCES public.tasks(task_id) ON DELETE CASCADE
) PARTITION BY RANGE (grade_level);
-- Partitions
CREATE TABLE public.solutions_middle_school PARTITION OF public.user_solutions FOR
VALUES
FROM (5) TO (9);
CREATE TABLE public.solutions_high_school PARTITION OF public.user_solutions FOR
VALUES
FROM (9) TO (12);
CREATE TABLE public.auth_credentials (
    user_id integer NOT NULL,
    login_name character varying(50) NOT NULL,
    password_hash text NOT NULL,
    CONSTRAINT auth_credentials_pkey PRIMARY KEY (user_id),
    CONSTRAINT auth_credentials_login_name_key UNIQUE (login_name),
    CONSTRAINT auth_credentials_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);
CREATE TABLE public.user_secure_data (
    user_id integer NOT NULL,
    email_encrypted bytea NOT NULL,
    CONSTRAINT user_secure_data_pkey PRIMARY KEY (user_id),
    CONSTRAINT user_secure_data_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);
CREATE SEQUENCE public.badges_badge_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.badges (
    badge_id integer DEFAULT nextval('public.badges_badge_id_seq'::regclass) NOT NULL,
    badge_name character varying(100) NOT NULL,
    xp_reward integer DEFAULT 50,
    CONSTRAINT badges_pkey PRIMARY KEY (badge_id),
    CONSTRAINT badges_badge_name_key UNIQUE (badge_name)
);
CREATE SEQUENCE public.achievements_achievement_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.achievements (
    achievement_id integer DEFAULT nextval(
        'public.achievements_achievement_id_seq'::regclass
    ) NOT NULL,
    user_id integer,
    badge_id integer,
    earned_at timestamp without time zone DEFAULT now(),
    CONSTRAINT achievements_pkey PRIMARY KEY (achievement_id),
    CONSTRAINT achievements_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE,
    CONSTRAINT achievements_badge_id_fkey FOREIGN KEY (badge_id) REFERENCES public.badges(badge_id) ON DELETE CASCADE
);
CREATE SEQUENCE public.system_audit_audit_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.system_audit (
    audit_id integer DEFAULT nextval('public.system_audit_audit_id_seq'::regclass) NOT NULL,
    event_type character varying(50) NOT NULL,
    description text,
    created_at timestamp without time zone DEFAULT now(),
    CONSTRAINT system_audit_pkey PRIMARY KEY (audit_id)
);
CREATE SEQUENCE public.motivational_tips_tip_id_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE public.motivational_tips (
    tip_id integer DEFAULT nextval('public.motivational_tips_tip_id_seq'::regclass) NOT NULL,
    min_complexity integer NOT NULL,
    tip_text text NOT NULL,
    CONSTRAINT motivational_tips_pkey PRIMARY KEY (tip_id)
);
-- Indexes
CREATE INDEX idx_profiles_xp ON public.user_profiles USING btree (xp DESC);
CREATE INDEX idx_tasks_topic ON public.tasks USING btree (topic_id);
CREATE UNIQUE INDEX idx_users_login ON public.users USING btree (nickname);
CREATE INDEX idx_solutions_user ON ONLY public.user_solutions USING btree (user_id);