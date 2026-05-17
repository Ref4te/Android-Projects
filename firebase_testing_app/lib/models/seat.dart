class Seat {
  final String id;
  final int row;
  final int column;
  bool isAvailable;
  final bool isVip;

  Seat({
    required this.id,
    required this.row,
    required this.column,
    this.isAvailable = true,
    required this.isVip,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'row': row,
      'column': column,
      'isAvailable': isAvailable,
      'isVip': isVip,
    };
  }

  factory Seat.fromMap(Map<String, dynamic> map) {
    return Seat(
      id: map['id'],
      row: map['row'],
      column: map['column'],
      isAvailable: map['isAvailable'],
      isVip: map['isVip'],
    );
  }
}
