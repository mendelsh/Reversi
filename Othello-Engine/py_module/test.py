from othello import Board, Bot

"""
b = Board()
black_bot = Bot.create(b, "pengwin", True)
white_bot = Bot.create(b, "greedy", False)

while not b.game_over():
    bot = black_bot if b.turn else white_bot
    move = bot.select_move(6)
    b.play(move.x, move.y)

b.display()
"""

def simulate_games(num_games, bot1, bot2, depth=4):
    pengwin_wins = 0
    greedy_wins = 0
    ties = 0
    black_wins = 0
    white_wins = 0

    for i in range(num_games):
        b = Board()
        if i % 2 == 0:
            black_bot = Bot.create(b, bot1, Board.BLACK_TURN)
            white_bot = Bot.create(b, bot2, Board.WHITE_TURN)
        else:
            black_bot = Bot.create(b, bot2, Board.BLACK_TURN)
            white_bot = Bot.create(b, bot1, Board.WHITE_TURN)
        while not b.game_over():
            bot = black_bot if b.turn == Board.BLACK_TURN else white_bot
            move = bot.select_move(depth)
            b.play(move.x, move.y)

        black, white = b.count_discs()
        if black == white:
            ties += 1
        elif black > white:
            if i % 2 == 0:
                pengwin_wins += 1
            else:
                greedy_wins += 1
            black_wins += 1
        else:
            if i % 2 == 0:
                greedy_wins += 1
            else:
                pengwin_wins += 1
            white_wins += 1

    return {
        "pengwin_wins": pengwin_wins,
        "greedy_wins": greedy_wins,
        "ties": ties,
        "black_wins": black_wins,
        "white_wins": white_wins,
    }

results = simulate_games(100, "pengwin", "greedy")
print("Pengwin wins:", results["pengwin_wins"])
print("Greedy wins:", results["greedy_wins"])
print("Ties:", results["ties"])
print("Black wins:", results["black_wins"])
print("White wins:", results["white_wins"])