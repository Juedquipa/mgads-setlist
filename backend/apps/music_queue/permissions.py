from rest_framework import permissions


class HasSessionToken(permissions.BasePermission):
    """
    Allows access only to clients that pass a valid X-Session-Token header.
    Automatically attaches `client_session` to the request.
    """

    def has_permission(self, request, view):
        token = request.headers.get("X-Session-Token")
        if not token:
            return False

        from .models import Session

        session = Session.objects.filter(token=token, is_active=True).first()
        if session:
            request.client_session = session
            return True
        return False
