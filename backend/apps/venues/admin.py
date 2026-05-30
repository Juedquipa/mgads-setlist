from django.contrib import admin

from .models import Catalog, Table


@admin.register(Table)
class TableAdmin(admin.ModelAdmin):
    list_display = ("name", "tenant", "qr_code_token", "is_active", "created_at")
    list_filter = ("tenant", "is_active")
    search_fields = ("name", "qr_code_token")


@admin.register(Catalog)
class CatalogAdmin(admin.ModelAdmin):
    list_display = ("tenant", "is_active", "created_at", "updated_at")
    list_filter = ("is_active",)
    search_fields = ("tenant__name",)
