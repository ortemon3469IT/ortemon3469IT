-- Security, Roles and RBAC
-- Roles Creation
DO $$ BEGIN IF NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = 'admin_teacher'
) THEN CREATE ROLE admin_teacher;
END IF;
IF NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = 'student_user'
) THEN CREATE ROLE student_user;
END IF;
IF NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = 'db_optimizer'
) THEN CREATE ROLE db_optimizer;
END IF;
IF NOT EXISTS (
    SELECT 1
    FROM pg_roles
    WHERE rolname = 'db_tester'
) THEN CREATE ROLE db_tester;
END IF;
END $$;
-- Schema Permissions
GRANT USAGE ON SCHEMA public TO admin_teacher,
    student_user,
    db_optimizer,
    db_tester;
-- Table Grants
GRANT ALL ON ALL TABLES IN SCHEMA public TO admin_teacher;
GRANT SELECT,
    INSERT,
    DELETE,
    TRIGGER,
    UPDATE ON ALL TABLES IN SCHEMA public TO db_optimizer;
GRANT SELECT ON public.tasks,
    public.topics,
    public.titles,
    public.user_profiles TO student_user;
GRANT SELECT ON public.tasks,
    public.topics,
    public.titles,
    public.system_audit TO db_tester;
-- Procedure Grants
REVOKE ALL ON PROCEDURE public.submit_answer_v4(integer, integer, integer, character varying)
FROM PUBLIC;
GRANT ALL ON PROCEDURE public.submit_answer_v4(integer, integer, integer, character varying) TO student_user,
    admin_teacher;