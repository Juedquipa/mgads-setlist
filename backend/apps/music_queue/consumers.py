import json

from channels.generic.websocket import AsyncWebsocketConsumer


class QueueConsumer(AsyncWebsocketConsumer):
    async def connect(self):
        self.tenant_id = self.scope["url_route"]["kwargs"]["tenant_id"]
        self.room_group_name = f"queue_{self.tenant_id}"

        # Join room group
        await self.channel_layer.group_add(self.room_group_name, self.channel_name)
        await self.accept()

    async def disconnect(self, close_code):
        # Leave room group
        await self.channel_layer.group_discard(self.room_group_name, self.channel_name)

    # Receive message from room group
    async def queue_update(self, event):
        message = event["message"]

        # Send message to WebSocket
        await self.send(
            text_data=json.dumps({"type": "queue_update", "message": message})
        )
