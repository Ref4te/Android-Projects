import 'dart:math';
import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import '../models/seat.dart';

class BookingService {
  final FirebaseFirestore _firestore = FirebaseFirestore.instance;
  final FirebaseAuth _auth = FirebaseAuth.instance;

  Future<void> generateScheduleForAdmin(String movieId, int durationMinutes) async {
    final user = _auth.currentUser;
    if (user == null || user.email != 'manat11@mail.ru') {
      throw Exception('Unauthorized: Only admin can generate schedules.');
    }

    final now = DateTime.now();
    final batch = _firestore.batch();

    for (int day = 0; day < 3; day++) {
      for (int hallId = 1; hallId <= 8; hallId++) {
        DateTime startTime = DateTime(now.year, now.month, now.day + day, 10, 0); // Start at 10 AM
        final endTimeLimit = DateTime(now.year, now.month, now.day + day, 23, 0); // Last session start limit

        while (startTime.isBefore(endTimeLimit)) {
          final endTime = startTime.add(Duration(minutes: durationMinutes));
          final sessionRef = _firestore.collection('sessions').doc();
          
          final int seatCount = 80 + Random().nextInt(21); // 80 to 100 seats
          final int rows = (seatCount / 10).ceil();
          final List<Map<String, dynamic>> seats = [];

          for (int r = 1; r <= rows; r++) {
            for (int c = 1; c <= 10; c++) {
              if (seats.length >= seatCount) break;
              bool isVip = r > (rows - 2);
              seats.add(Seat(
                id: 'r${r}c$c',
                row: r,
                column: c,
                isAvailable: true,
                isVip: isVip,
              ).toMap());
            }
          }

          batch.set(sessionRef, {
            'movieId': movieId,
            'startTime': startTime,
            'endTime': endTime,
            'hallId': hallId,
            'seats': seats,
          });

          startTime = endTime.add(const Duration(minutes: 30)); // 30 min cleaning break
        }
      }
    }
    await batch.commit();
  }

  Future<void> bookSeat(String sessionId, String seatId) async {
    final user = _auth.currentUser;
    if (user == null) throw Exception('User must be logged in');

    final sessionRef = _firestore.collection('sessions').doc(sessionId);

    return _firestore.runTransaction((transaction) async {
      final snapshot = await transaction.get(sessionRef);
      if (!snapshot.exists) throw Exception('Session not found');

      List<dynamic> seats = List.from(snapshot.get('seats'));
      int seatIndex = seats.indexWhere((s) => s['id'] == seatId);

      if (seatIndex == -1) throw Exception('Seat not found');
      if (seats[seatIndex]['isAvailable'] == false) throw Exception('Seat already booked');

      seats[seatIndex]['isAvailable'] = false;

      transaction.update(sessionRef, {'seats': seats});

      final ticketRef = _firestore.collection('tickets').doc();
      transaction.set(ticketRef, {
        'userId': user.uid,
        'sessionId': sessionId,
        'movieId': snapshot.get('movieId'),
        'hallId': snapshot.get('hallId'),
        'startTime': snapshot.get('startTime'),
        'seatId': seatId,
        'bookedAt': FieldValue.serverTimestamp(),
      });
    });
  }

  Stream<QuerySnapshot> getSessions(String movieId) {
    return _firestore
        .collection('sessions')
        .where('movieId', isEqualTo: movieId)
        .snapshots();
  }

  Stream<DocumentSnapshot> getSessionUpdates(String sessionId) {
    return _firestore.collection('sessions').doc(sessionId).snapshots();
  }

  Stream<QuerySnapshot> getMyTickets() {
    final user = _auth.currentUser;
    if (user == null) return const Stream.empty();
    return _firestore
        .collection('tickets')
        .where('userId', isEqualTo: user.uid)
        .orderBy('bookedAt', descending: true)
        .snapshots();
  }
}
