const jsonDateToFormatted = (input) => {
    // Regex removes the [UTC]
    const cleaned = input.replace(/\[.*\]$/, "");

    // Creates new Date object
    const date = new Date(cleaned);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();

    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');

    return `${day}/${month}/${year}, ${hours}:${minutes}`;
}

export default jsonDateToFormatted;