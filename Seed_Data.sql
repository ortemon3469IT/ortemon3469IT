-- Seed Initial Data
INSERT INTO public.titles (title_id, title_name, min_level)
VALUES (6, 'Новичок', 1),
    (7, 'Эрудит', 3),
    (8, 'Архимед', 5) ON CONFLICT (title_id) DO NOTHING;
INSERT INTO public.topics (topic_id, topic_name, description)
VALUES (6, 'Геометрия', 'Задачи на углы и площади') ON CONFLICT (topic_id) DO NOTHING;
INSERT INTO public.users (user_id, nickname, user_role, created_at)
VALUES (
        1,
        'demo_student',
        'student',
        '2026-10-07 14:44:30.889464'
    ) ON CONFLICT (user_id) DO NOTHING;
INSERT INTO public.auth_credentials (user_id, login_name, password_hash)
VALUES (
        1,
        'demo_student',
        '$2a$06$bQUOn06GoEWn45kQ3BDKyuFHMPSJ/JCnv1FmpQEpEA.70UnnN75yW'
    ) ON CONFLICT (user_id) DO NOTHING;
INSERT INTO public.tasks (
        task_id,
        topic_id,
        task_text,
        correct_answer,
        complexity,
        start_date,
        deadline
    )
VALUES (
        6,
        6,
        'Чему равна сумма углов треугольника?',
        '180',
        3,
        '2026-10-07 14:44:30.889464',
        '2026-11-06 14:44:30.889464'
    ) ON CONFLICT (task_id) DO NOTHING;