-- Triggers
CREATE TRIGGER trg_audit_tasks
AFTER
INSERT ON public.tasks FOR EACH ROW EXECUTE FUNCTION public.log_new_task();
CREATE TRIGGER trg_create_profile
AFTER
INSERT ON public.users FOR EACH ROW EXECUTE FUNCTION public.create_user_profile();
CREATE TRIGGER trg_validate_complexity BEFORE
INSERT
    OR
UPDATE ON public.tasks FOR EACH ROW EXECUTE FUNCTION public.check_task_complexity();