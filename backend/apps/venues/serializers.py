from rest_framework import serializers

from .models import Catalog, Table


class TableSerializer(serializers.ModelSerializer):
    class Meta:
        model = Table
        fields = ["id", "name", "qr_code_token", "is_active", "created_at"]
        read_only_fields = ["id", "created_at", "qr_code_token"]


class CatalogSerializer(serializers.ModelSerializer):
    class Meta:
        model = Catalog
        fields = ["id", "is_active", "created_at", "updated_at"]
        read_only_fields = ["id", "created_at", "updated_at"]
