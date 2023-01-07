import {fetchJson} from "./utilities/helpers";
const gamesList = document.getElementById("games-list");

fetchJson('/api/games').then(games => {
    console.log('games: ', games);
    const gamesInfo = games.map(game =>
        game['created'].toLocaleString() + ', ' +
        game['gamePlayers'].map(gamePlayer => gamePlayer['player']['username']).sort().join(', '));

    gamesInfo.forEach(createListOfGamesInfo);
});

const createListOfGamesInfo = (gameInfo) => {
    const listItem = document.createElement('li');
    const listItemText = document.createTextNode(gameInfo);
    listItem.appendChild(listItemText);
    gamesList.appendChild(listItem);
}