from django.db import migrations, models


def _generate_unique_staff_pin(User):
    from django.utils.crypto import get_random_string

    while True:
        pin = get_random_string(6, allowed_chars="0123456789")
        if not User.objects.filter(staff_pin=pin).exists():
            return pin


def populate_staff_pins(apps, schema_editor):
    User = apps.get_model("users", "User")
    staff_users = User.objects.filter(
        role__in=["ADMIN", "WAITER"], staff_pin__isnull=True
    )

    for user in staff_users:
        user.staff_pin = _generate_unique_staff_pin(User)
        user.save(update_fields=["staff_pin"])


def noop_reverse(apps, schema_editor):
    pass


class Migration(migrations.Migration):
    dependencies = [
        ("users", "0001_initial"),
    ]

    operations = [
        migrations.AddField(
            model_name="user",
            name="staff_pin",
            field=models.CharField(blank=True, max_length=6, null=True, unique=True),
        ),
        migrations.RunPython(populate_staff_pins, noop_reverse),
    ]
