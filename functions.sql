-- Functions & Procedures
CREATE OR REPLACE FUNCTION public.check_task_complexity() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.complexity < 1
    OR NEW.complexity > 10 THEN RAISE EXCEPTION 'Ошибка: сложность задачи должна быть от 1 до 10';
END IF;
RETURN NEW;
END;
$$;
CREATE OR REPLACE FUNCTION public.create_user_profile() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
INSERT INTO user_profiles (user_id)
VALUES (NEW.user_id) ON CONFLICT (user_id) DO NOTHING;
RETURN NEW;
END;
$$;
CREATE OR REPLACE FUNCTION public.log_new_task() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN
INSERT INTO system_audit (event_type, description)
VALUES (
        'INSERT_TASK',
        'Добавлена задача № ' || NEW.task_id || ' по теме ' || COALESCE(NEW.topic_id::TEXT, 'без темы')
    );
RETURN NEW;
END;
$$;
CREATE OR REPLACE PROCEDURE public.submit_answer_v4(
        IN p_user_id integer,
        IN p_task_id integer,
        IN p_grade_level integer,
        IN p_user_answer character varying
    ) LANGUAGE plpgsql SECURITY DEFINER
SET search_path TO 'public' AS $$
DECLARE c_min_grade CONSTANT INT := 5;
c_max_grade CONSTANT INT := 11;
c_xp_reward CONSTANT INT := 20;
c_coin_reward CONSTANT INT := 10;
c_xp_per_level CONSTANT INT := 100;
v_correct_answer VARCHAR;
v_start_date TIMESTAMP;
v_deadline TIMESTAMP;
v_is_correct BOOLEAN;
v_already_solved BOOLEAN;
BEGIN IF p_grade_level < c_min_grade
OR p_grade_level > c_max_grade THEN RAISE EXCEPTION 'Ошибка: класс должен быть от % до %',
c_min_grade,
c_max_grade;
END IF;
SELECT correct_answer,
    start_date,
    deadline INTO v_correct_answer,
    v_start_date,
    v_deadline
FROM tasks
WHERE task_id = p_task_id;
IF NOT FOUND THEN RAISE EXCEPTION 'Ошибка: задачи с номером % нет',
p_task_id;
END IF;
IF v_start_date IS NOT NULL
AND NOW() < v_start_date THEN RAISE EXCEPTION 'Ошибка: эта задача ещё не открыта';
END IF;
IF v_deadline IS NOT NULL
AND NOW() > v_deadline THEN RAISE EXCEPTION 'Ошибка: срок сдачи этой задачи уже вышел';
END IF;
v_is_correct := COALESCE(
    LOWER(TRIM(v_correct_answer)) = LOWER(TRIM(p_user_answer)),
    FALSE
);
PERFORM 1
FROM user_profiles
WHERE user_id = p_user_id FOR
UPDATE;
IF NOT FOUND THEN RAISE EXCEPTION 'Ошибка: ученика с номером % нет',
p_user_id;
END IF;
SELECT EXISTS (
        SELECT 1
        FROM user_solutions
        WHERE user_id = p_user_id
            AND task_id = p_task_id
            AND is_correct
    ) INTO v_already_solved;
IF v_is_correct
AND NOT v_already_solved THEN
UPDATE user_profiles
SET xp = xp + c_xp_reward,
    coins = coins + c_coin_reward,
    current_streak = current_streak + 1,
    current_level = (xp + c_xp_reward) / c_xp_per_level + 1,
    title_id = COALESCE(
        (
            SELECT t.title_id
            FROM titles t
            WHERE t.min_level <= (user_profiles.xp + c_xp_reward) / c_xp_per_level + 1
            ORDER BY t.min_level DESC
            LIMIT 1
        ), title_id
    )
WHERE user_id = p_user_id;
ELSIF NOT v_is_correct THEN
UPDATE user_profiles
SET current_streak = 0
WHERE user_id = p_user_id;
END IF;
INSERT INTO user_solutions (user_id, task_id, grade_level, is_correct)
VALUES (
        p_user_id,
        p_task_id,
        p_grade_level,
        v_is_correct
    );
INSERT INTO system_audit (event_type, description)
VALUES (
        'ANSWER_SUBMITTED',
        'Пользователь ' || p_user_id || ' сдал ответ на задачу ' || p_task_id
    );
END;
$$;