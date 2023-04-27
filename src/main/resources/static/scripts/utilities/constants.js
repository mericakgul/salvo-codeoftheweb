export const domainUrl = 'http://localhost:8080';
export const allShipTypes = [
    {id: 'carrier', name: 'Carrier', size: 5},
    {id: 'battleship', name: 'Battleship', size: 4},
    {id: 'submarine', name: 'Submarine', size: 3},
    {id: 'destroyer', name: 'Destroyer', size: 3},
    {id: 'patrolboat', name: 'Patrol Boat', size: 2}
];

export const game_history =
    {
        1: [
            {
                3: {
                    "ships_hit": {
                        "carrier": ["a1", "a2", "a3", "a4", "a5"]
                    },
                    "ship_number_left": 3
                }
            },
            {
                2: {
                    "ships_hit": {
                        "submarine": ["f1", "f2", "f3"]
                    },
                    "ship_number_left": 4
                }
            },
            {
                1: {
                    "ships_hit": {
                        "destroyer": ["g7", "h7"],
                        "submarine": ["j3"]
                    },
                    "ship_number_left": 5
                }
            }

        ],
        2: [
            {
                1: {
                    "ships_hit": {
                        "destroyer": ["c1", "c2", "c3", "c4", "c5"]
                    },
                    "ship_number_left": 5
                }
            },
            {
                2: {
                    "ships_hit": {
                        "destroyer": ["d4", "e4"],
                        "Patrol Boat": ["i10", "j10"]
                    },
                    "ship_number_left": 5
                }
            }
        ]
    }