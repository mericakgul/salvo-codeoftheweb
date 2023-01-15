import {fetchJson} from "./utilities/helpers.js";

const gamesList = document.getElementById("games-list");

fetchJson('/api/games').then(games => {
    console.log('games: ', games);
    createGameInfo(games);
    const playerList = changeJsonStructureForLeaderboard(games);
    console.log('playerList', playerList);
});

function createGameInfo(games) {
    const gamesInfo = games.map(game =>
        game['created'].toLocaleString() + ', ' +
        game['gamePlayers'].map(gamePlayer => gamePlayer['player']['username']).sort().join(', '));
    gamesInfo.forEach(createListOfGames);
}

const createListOfGames = (gameInfo) => {
    const listItem = document.createElement('li');
    const listItemText = document.createTextNode(gameInfo);
    listItem.appendChild(listItemText);
    gamesList.appendChild(listItem);
}

function changeJsonStructureForLeaderboard(games) {
    return games.reduce((playerList, {gamePlayers}) => {
        const playerUsernamesGamePlayer = gamePlayers.map(({player}) => player['username']);
        // playerList = playerList.concat(playerUsernamesGamePlayer); To be able to use includes method we used foreach. Otherwise, duplicated values are also added to array.
        playerUsernamesGamePlayer.forEach(username => {
            if (!playerList.includes(username)) {
                playerList.push(username);
            }
        });
        return playerList;
    }, []);
}