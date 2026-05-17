import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import '../services/booking_service.dart';

class TicketPage extends StatelessWidget {
  const TicketPage({super.key});

  @override
  Widget build(BuildContext context) {
    final BookingService bookingService = BookingService();

    return Scaffold(
      appBar: AppBar(title: const Text('My Tickets')),
      body: StreamBuilder<QuerySnapshot>(
        stream: bookingService.getMyTickets(),
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          }
          final tickets = snapshot.data?.docs ?? [];
          if (tickets.isEmpty) {
            return const Center(child: Text('You have no tickets yet.'));
          }

          return ListView.builder(
            itemCount: tickets.length,
            itemBuilder: (context, index) {
              final ticket = tickets[index].data() as Map<String, dynamic>;
              final startTime = (ticket['startTime'] as Timestamp).toDate();

              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                child: ListTile(
                  leading: const Icon(Icons.confirmation_number, color: Colors.deepPurple),
                  title: Text('Movie ID: ${ticket['movieId']}'),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('Date: ${startTime.day}/${startTime.month}/${startTime.year}'),
                      Text('Time: ${startTime.hour}:${startTime.minute.toString().padLeft(2, '0')}'),
                      Text('Hall: ${ticket['hallId']} | Seat: ${ticket['seatId']}'),
                    ],
                  ),
                  isThreeLine: true,
                ),
              );
            },
          );
        },
      ),
    );
  }
}
