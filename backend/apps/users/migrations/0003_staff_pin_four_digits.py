from django.db import migrations, models


def _generate_unique_staff_pin(User):
    from django.utils.crypto import get_random_string

    while True:
        pin = get_random_string(4, allowed_chars="0123456789")
        if not User.objects.filter(staff_pin=pin).exists():
            return pin


def repopulate_staff_pins(apps, schema_editor):
    User = apps.get_model("users", "User")
    staff_users = User.objects.filter(role__in=["ADMIN", "WAITER"])

    for user in staff_users:
        user.staff_pin = _generate_unique_staff_pin(User)
        user.save(update_fields=["staff_pin"])


class Migration(migrations.Migration):
    dependencies = [
        ("users", "0002_staff_pin"),
    ]

    operations = [
        migrations.AlterField(
            model_name="user",
            name="staff_pin",
            field=models.CharField(blank=True, max_length=4, null=True, unique=True),
        ),
        migrations.RunPython(repopulate_staff_pins, migrations.RunPython.noop),
    ]
