import json
import requests
from bs4 import BeautifulSoup
import time

# Define the URL of the website you want to scrape
url = "https://www.forexfactory.com/calendar"

# Set headers to mimic a real browser
headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'}

# Send an HTTP request to the URL
proxies = {
    'http': 'http://10.10.1.10:3128',
    'https': 'http://10.10.1.10:1080',
}
session = requests.Session()
response = session.get(url, headers=headers)


# Define the ForexNewsItem class
class ForexNewsItem:
    def __init__(self, time, name, currency, status, is_done):
        self.time = time
        self.name = name
        self.currency = currency
        self.status = status
        self.is_done = is_done

    def to_dict(self):
        return {
            "time": self.time,
            "name": self.name,
            "currency": self.currency,
            "status": self.status,
            "is_done": self.is_done
        }

# Function to extract table data and create ForexNewsItem objects
def extract_table_data(soup):
    news_items = []
    table = soup.find('table', {'class': 'calendar__table'})
    # print(table)
    if not table:
        print("No table found")
        return news_items

    rows = table.find_all('tr', {'class': 'calendar__row'})
    date_tracker=''
    hour=''
    hour_ = ''
    for row in rows:
        cells = [cell.text.strip() for cell in row.find_all('td')]
        if len(cells) == 5:  # Ensure there are at least 5 cells to avoid IndexError
            pass
            # if cells[0] != '':
            #     hour=cells[0]
            #     hour_ = hour
            #     time = date_tracker + " " + cells[0]
            # else:
            #     time = date_tracker + " " + hour_
            #
            # name = cells[1]+"5-5"
            # currency = cells[1]
            # status = cells[3]
            # is_done = cells[4] if len(cells) > 4 else ""
            # news_item = ForexNewsItem(time, name, currency, status, is_done)
            # news_items.append(news_item)
        elif len(cells) >= 6:  # Ensure there are at least 5 cells to avoid IndexError
            if cells[1] != '':
                hour=cells[1]
                hour_ = hour
                time = date_tracker + " " + cells[1]
            elif cells[0] != '':
                hour = cells[0]
                hour_ = hour
                time = date_tracker + " " + cells[0]
            else:
                time = date_tracker + " " + hour_

            name = cells[5] if len(cells[1]) > 4 else cells[4]
            currency = cells[3]  if len(cells[3]) > 1 else cells[2]
            status = row.find('td', {'class': 'calendar__impact'}).span['class'][1].split('-')[-1]
            is_done = "Not"
            news_item = ForexNewsItem(time, name, currency, status, is_done)
            news_items.append(news_item)
        elif len(cells) == 1:
            date_tracker=cells[0]

    return news_items



def get_news_items():
    # Check if the request was successful
    if response.status_code == 200:
        # Parse the HTML content of the page
        soup = BeautifulSoup(response.content, 'html.parser')
        # Extract table data
        news_items = extract_table_data(soup)
        # Convert to JSON
        news_items_json = json.dumps([item.to_dict() for item in news_items], indent=4)
        return news_items_json
    else:
        return [f'Failed to retrieve the webpage. Status code: {response.status_code}']

# print(get_news_items())