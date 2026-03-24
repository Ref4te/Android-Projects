import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  runApp(const TicTacToeApp());
}

class TicTacToeApp extends StatelessWidget {
  const TicTacToeApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Крестики-нолики',
      theme: ThemeData(
        scaffoldBackgroundColor: const Color(0xFFF2F2F7),
        useMaterial3: true,
      ),
      home: const TicTacToePage(),
    );
  }
}

class TicTacToePage extends StatefulWidget {
  const TicTacToePage({super.key});

  @override
  State<TicTacToePage> createState() => _TicTacToePageState();
}

class _TicTacToePageState extends State<TicTacToePage> {
  final List<String> board = List.filled(9, '');
  final List<int> xMoves = [];
  final List<int> oMoves = [];

  String currentPlayer = 'X';
  String statusText = 'Ход игрока X';
  bool gameOver = false;
  bool isLoading = true;

  static const String keyBoard = 'board';
  static const String keyXMoves = 'xMoves';
  static const String keyOMoves = 'oMoves';
  static const String keyCurrentPlayer = 'currentPlayer';
  static const String keyStatusText = 'statusText';
  static const String keyGameOver = 'gameOver';

  @override
  void initState() {
    super.initState();
    _loadGame();
  }

  @override
  void dispose() {
    _saveGame();
    super.dispose();
  }

  Future<void> _saveGame() async {
    final prefs = await SharedPreferences.getInstance();

    await prefs.setString(keyBoard, board.join(','));
    await prefs.setStringList(
      keyXMoves,
      xMoves.map((e) => e.toString()).toList(),
    );
    await prefs.setStringList(
      keyOMoves,
      oMoves.map((e) => e.toString()).toList(),
    );
    await prefs.setString(keyCurrentPlayer, currentPlayer);
    await prefs.setString(keyStatusText, statusText);
    await prefs.setBool(keyGameOver, gameOver);
  }

  Future<void> _loadGame() async {
    final prefs = await SharedPreferences.getInstance();

    final savedBoard = prefs.getString(keyBoard);
    final savedXMoves = prefs.getStringList(keyXMoves);
    final savedOMoves = prefs.getStringList(keyOMoves);
    final savedCurrentPlayer = prefs.getString(keyCurrentPlayer);
    final savedStatusText = prefs.getString(keyStatusText);
    final savedGameOver = prefs.getBool(keyGameOver);

    if (savedBoard != null) {
      final loadedBoard = savedBoard.split(',');
      if (loadedBoard.length == 9) {
        for (int i = 0; i < 9; i++) {
          board[i] = loadedBoard[i];
        }
      }
    }

    xMoves.clear();
    if (savedXMoves != null) {
      xMoves.addAll(savedXMoves.map(int.parse));
    }

    oMoves.clear();
    if (savedOMoves != null) {
      oMoves.addAll(savedOMoves.map(int.parse));
    }

    currentPlayer = savedCurrentPlayer ?? 'X';
    statusText = savedStatusText ?? 'Ход игрока X';
    gameOver = savedGameOver ?? false;

    if (mounted) {
      setState(() {
        isLoading = false;
      });
    }
  }

  void onCellTap(int index) {
    if (gameOver || board[index].isNotEmpty) return;

    setState(() {
      final currentMoves = currentPlayer == 'X' ? xMoves : oMoves;

      if (currentMoves.length == 3) {
        final removedIndex = currentMoves.removeAt(0);
        board[removedIndex] = '';
      }

      board[index] = currentPlayer;
      currentMoves.add(index);

      if (_checkWinner(currentPlayer)) {
        statusText = 'Победил игрок $currentPlayer';
        gameOver = true;
      } else {
        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
        statusText = 'Ход игрока $currentPlayer';
      }
    });

    _saveGame();
  }

  bool _checkWinner(String player) {
    const winLines = [
      [0, 1, 2],
      [3, 4, 5],
      [6, 7, 8],
      [0, 3, 6],
      [1, 4, 7],
      [2, 5, 8],
      [0, 4, 8],
      [2, 4, 6],
    ];

    for (final line in winLines) {
      if (board[line[0]] == player &&
          board[line[1]] == player &&
          board[line[2]] == player) {
        return true;
      }
    }
    return false;
  }

  void resetGame() {
    setState(() {
      for (int i = 0; i < board.length; i++) {
        board[i] = '';
      }
      xMoves.clear();
      oMoves.clear();
      currentPlayer = 'X';
      statusText = 'Ход игрока X';
      gameOver = false;
    });

    _saveGame();
  }

  bool _isNextToDisappear(int index) {
    final queue = board[index] == 'X' ? xMoves : oMoves;
    return queue.length == 3 && queue.isNotEmpty && queue.first == index;
  }

  Widget _buildMark(String value, bool faded) {
    if (value == 'X') {
      return Text(
        '✕',
        style: TextStyle(
          fontSize: 52,
          fontWeight: FontWeight.w700,
          color: faded
              ? CupertinoColors.systemBlue.withOpacity(0.35)
              : CupertinoColors.systemBlue,
        ),
      );
    }

    if (value == 'O') {
      return Text(
        '◯',
        style: TextStyle(
          fontSize: 52,
          fontWeight: FontWeight.w700,
          color: faded
              ? CupertinoColors.systemRed.withOpacity(0.35)
              : CupertinoColors.systemRed,
        ),
      );
    }

    return const SizedBox.shrink();
  }

  Widget _buildCell(int index) {
    final value = board[index];
    final faded = value.isNotEmpty && _isNextToDisappear(index);

    return GestureDetector(
      onTap: () => onCellTap(index),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 220),
        curve: Curves.easeInOut,
        decoration: BoxDecoration(
          color: CupertinoColors.white,
          borderRadius: BorderRadius.circular(18),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.06),
              blurRadius: 10,
              offset: const Offset(0, 4),
            ),
          ],
          border: Border.all(
            color: faded
                ? CupertinoColors.systemGrey3
                : CupertinoColors.systemGrey4,
            width: 1.3,
          ),
        ),
        child: Center(
          child: _buildMark(value, faded),
        ),
      ),
    );
  }

  Widget _buildLegend() {
    String hint;
    if (currentPlayer == 'X') {
      hint = xMoves.length == 3
          ? 'У X следующая бледная фигура исчезнет'
          : 'X может поставить фигуру';
    } else {
      hint = oMoves.length == 3
          ? 'У O следующая бледная фигура исчезнет'
          : 'O может поставить фигуру';
    }

    return Column(
      children: [
        Text(
          statusText,
          style: const TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w700,
            color: CupertinoColors.label,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          hint,
          textAlign: TextAlign.center,
          style: const TextStyle(
            fontSize: 15,
            color: CupertinoColors.secondaryLabel,
          ),
        ),
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    final boardSize = MediaQuery.of(context).size.width - 32;

    if (isLoading) {
      return const Scaffold(
        body: Center(
          child: CupertinoActivityIndicator(),
        ),
      );
    }

    return Scaffold(
      appBar: AppBar(
        elevation: 0,
        centerTitle: true,
        backgroundColor: const Color(0xFFF2F2F7),
        title: const Text(
          'Крестики-нолики',
          style: TextStyle(
            fontWeight: FontWeight.w700,
            color: CupertinoColors.label,
          ),
        ),
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              const SizedBox(height: 12),
              _buildLegend(),
              const SizedBox(height: 28),
              SizedBox(
                width: boardSize,
                height: boardSize,
                child: GridView.builder(
                  physics: const NeverScrollableScrollPhysics(),
                  itemCount: 9,
                  gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: 3,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                  ),
                  itemBuilder: (context, index) => _buildCell(index),
                ),
              ),
              const SizedBox(height: 28),
              SizedBox(
                width: double.infinity,
                child: CupertinoButton.filled(
                  borderRadius: BorderRadius.circular(16),
                  onPressed: resetGame,
                  child: const Text(
                    'Новая игра',
                    style: TextStyle(fontSize: 17, fontWeight: FontWeight.w600),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}