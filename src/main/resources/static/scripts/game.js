const gridContainer = document.querySelector('.grid-container');
const gridSize = 10;
gridContainer.setAttribute('style', `grid-template-columns:repeat(${gridSize + 1}, 1fr)`); // To be able to have dynamic grid size in case we want different size of grid.
let rowLetter = 'A'; // The beginning letter of the row.

createGrid();

function createGrid() {
    for (let rowNo = 0; rowNo <= gridSize; rowNo++) {
        if (rowNo === 0) {
            createColumnHeaders();
        } else {
            createRowCells();
        }
    }
}

function createColumnHeaders() {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo !== 0) {
            const gridItemText = document.createTextNode(`${columnNo}`);
            gridItem.appendChild(gridItemText);
        }
        gridContainer.appendChild(gridItem);
    }
}

function createRowCells() {
    for (let columnNo = 0; columnNo <= gridSize; columnNo++) {
        const gridItem = document.createElement('label');
        gridItem.setAttribute('class', 'grid-item');
        if (columnNo === 0) {
            const gridItemText = document.createTextNode(rowLetter);
            gridItem.appendChild(gridItemText);
        } else {
            gridItem.setAttribute('id', `${rowLetter + columnNo}`);
        }
        gridContainer.appendChild(gridItem);
    }
    rowLetter = nextChar(rowLetter);
}

function nextChar(c) {
    return String.fromCharCode(c.charCodeAt(0) + 1);
}