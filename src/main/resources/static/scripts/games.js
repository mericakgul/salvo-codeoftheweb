const gamesList = document.getElementById("games-list");

const fetchGames = url =>
    fetch(url).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('error: ' + response.statusText);
        }
    });

fetchGames('/api/games').then(games => {
    const gamesInfo = games.map(game =>
        game['created'].toLocaleString() +
        game['gamePlayers'].map(gamePlayer => gamePlayer['player']['username']).sort().join(','));

    gamesInfo.forEach(createListOfGamesInfo);
});

const createListOfGamesInfo = (gameInfo) => {
    const listItem = document.createElement('li');
    const listItemText = document.createTextNode(gameInfo);
    listItem.appendChild(listItemText);
    gamesList.appendChild(listItem);
}