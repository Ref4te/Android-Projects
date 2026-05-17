import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:flutter/material.dart';
import '../services/booking_service.dart';

class BookingScreen extends StatefulWidget {
  final String movieId;
  const BookingScreen({super.key, required this.movieId});

  @override
  State<BookingScreen> createState() => _BookingScreenState();
}

class _BookingScreenState extends State<BookingScreen> {
  final BookingService _bookingService = BookingService();
  String? _selectedSessionId;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Book Seats')),
      body: Column(
        children: [
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text('Select Session Time', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
          ),
          SizedBox(
            height: 100,
            child: StreamBuilder<QuerySnapshot>(
              stream: _bookingService.getSessions(widget.movieId),
              builder: (context, snapshot) {
                if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
                final sessions = snapshot.data!.docs;
                if (sessions.isEmpty) return const Center(child: Text('No sessions available'));

                return ListView.builder(
                  scrollDirection: Axis.horizontal,
                  itemCount: sessions.length,
                  itemBuilder: (context, index) {
                    final session = sessions[index];
                    final startTime = (session['startTime'] as Timestamp).toDate();
                    final isSelected = _selectedSessionId == session.id;

                    return Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8.0),
                      child: ChoiceChip(
                        label: Text('${startTime.day}/${startTime.month} ${startTime.hour}:${startTime.minute.toString().padLeft(2, '0')}'),
                        selected: isSelected,
                        onSelected: (selected) {
                          setState(() {
                            _selectedSessionId = session.id;
                          });
                        },
                      ),
                    );
                  },
                );
              },
            ),
          ),
          if (_selectedSessionId != null)
            Expanded(
              child: StreamBuilder<DocumentSnapshot>(
                stream: _bookingService.getSessionUpdates(_selectedSessionId!),
                builder: (context, snapshot) {
                  if (!snapshot.hasData) return const Center(child: CircularProgressIndicator());
                  final data = snapshot.data!.data() as Map<String, dynamic>;
                  final seats = List<Map<String, dynamic>>.from(data['seats']);

                  return Column(
                    children: [
                      const Padding(
                        padding: EdgeInsets.symmetric(vertical: 20),
                        child: Text('SCREEN', style: TextStyle(letterSpacing: 10, fontWeight: FontWeight.bold)),
                      ),
                      Expanded(
                        child: GridView.builder(
                          padding: const EdgeInsets.all(16),
                          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                            crossAxisCount: 10,
                            mainAxisSpacing: 8,
                            crossAxisSpacing: 8,
                          ),
                          itemCount: seats.length,
                          itemBuilder: (context, index) {
                            final seat = seats[index];
                            final bool isAvailable = seat['isAvailable'];
                            final bool isVip = seat['isVip'];

                            return GestureDetector(
                              onTap: isAvailable
                                  ? () => _confirmBooking(context, seat['id'])
                                  : null,
                              child: Container(
                                decoration: BoxDecoration(
                                  color: isAvailable
                                      ? (isVip ? Colors.amber : Colors.green)
                                      : Colors.grey,
                                  borderRadius: BorderRadius.circular(4),
                                ),
                                child: Center(
                                  child: Text(
                                    seat['id'],
                                    style: const TextStyle(fontSize: 8, color: Colors.white),
                                  ),
                                ),
                              ),
                            );
                          },
                        ),
                      ),
                    ],
                  );
                },
              ),
            ),
        ],
      ),
    );
  }

  void _confirmBooking(BuildContext context, String seatId) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Confirm Booking'),
        content: Text('Do you want to book seat $seatId?'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              try {
                await _bookingService.bookSeat(_selectedSessionId!, seatId);
                ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Booking successful!')));
              } catch (e) {
                ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
              }
            },
            child: const Text('Confirm'),
          ),
        ],
      ),
    );
  }
}
