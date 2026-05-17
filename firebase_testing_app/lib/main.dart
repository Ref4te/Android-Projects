import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'firebase_options.dart';
import 'screens/booking_screen.dart';
import 'screens/ticket_page.dart';
import 'services/booking_service.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(
    options: DefaultFirebaseOptions.currentPlatform,
  );
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Cinema Booking',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const HomeScreen(),
    );
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  final auth = FirebaseAuth.instance;
  final bookingService = BookingService();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Cinema App')),
      body: Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: StreamBuilder<User?>(
            stream: auth.authStateChanges(),
            builder: (context, snapshot) {
              final user = snapshot.data;
              if (user == null) {
                return Column(
                  children: [
                    TextField(
                      controller: _emailController,
                      decoration: const InputDecoration(labelText: 'Email (manat11@mail.ru for admin)'),
                    ),
                    TextField(
                      controller: _passwordController,
                      decoration: const InputDecoration(labelText: 'Password'),
                      obscureText: true,
                    ),
                    const SizedBox(height: 20),
                    ElevatedButton(
                      onPressed: () async {
                        try {
                          await auth.signInWithEmailAndPassword(
                            email: _emailController.text.trim(),
                            password: _passwordController.text.trim(),
                          );
                        } catch (e) {
                          // Если пользователя нет, пробуем создать (для теста)
                          try {
                            await auth.createUserWithEmailAndPassword(
                              email: _emailController.text.trim(),
                              password: _passwordController.text.trim(),
                            );
                          } catch (err) {
                            ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Auth Error: $err')));
                          }
                        }
                      },
                      child: const Text('Login / Register'),
                    ),
                    TextButton(
                      onPressed: () async {
                        try {
                          await auth.signInAnonymously();
                        } catch (e) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            const SnackBar(content: Text('Anonymous Auth is disabled in Firebase Console')),
                          );
                        }
                      },
                      child: const Text('Login Anonymously'),
                    ),
                  ],
                );
              }
              return Column(
                children: [
                  Text('Logged in as: ${user.email ?? "Anonymous"}', style: const TextStyle(fontWeight: FontWeight.bold)),
                  const SizedBox(height: 10),
                  if (user.email == 'manat11@mail.ru')
                    Card(
                      color: Colors.amber.shade100,
                      child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: Column(
                          children: [
                            const Text('ADMIN PANEL', style: TextStyle(fontWeight: FontWeight.bold)),
                            ElevatedButton(
                              onPressed: () async {
                                try {
                                  await bookingService.generateScheduleForAdmin('movie_123', 120);
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    const SnackBar(content: Text('Schedule generated!')),
                                  );
                                } catch (e) {
                                  ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Error: $e')));
                                }
                              },
                              child: const Text('Generate Schedule'),
                            ),
                          ],
                        ),
                      ),
                    ),
                  const SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const BookingScreen(movieId: 'movie_123'),
                        ),
                      );
                    },
                    child: const Text('Go to Booking Screen'),
                  ),
                  const SizedBox(height: 10),
                  ElevatedButton(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => const TicketPage(),
                        ),
                      );
                    },
                    child: const Text('My Tickets'),
                  ),
                  const SizedBox(height: 20),
                  TextButton(
                    onPressed: () => auth.signOut(),
                    child: const Text('Logout', style: TextStyle(color: Colors.red)),
                  ),
                ],
              );
            },
          ),
        ),
      ),
    );
  }
}
