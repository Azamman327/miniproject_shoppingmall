#pip install pymysql
#pip install requests

import product_client

def login():
  userId = input('id를 입력: ')
  password = input('비밀번호를 입력: ')

  userInfo = {
    'userId': userId,
    'password': password
  }

  userId = product_client.login(userInfo)
  if userId != '':
    print('로그인 성공')
  else:
    print('로그인 실패') 
  return userId

def logout():
  print('로그아웃 되었습니다.')
  return

def signUp():
  userId = input('가입할 id를 입력: ')
  password = input('비밀번호를 입력: ')

  userInfo = {
    'userId': userId,
    'password': password
  }

  userId = product_client.signUp(userInfo)
  if userId != '':
    print('정상적으로 회원가입 되었습니다.')
  else:
    print('이미 존재하는 아이디입니다.')
  return userId

def userController():
  rslt = ''
  while (rslt == ''):
    n = int(input('로그인을 원하면 1, 회원가입을 원하면 2를 입력: '))
    if n == 1:
      rslt = login()       
    elif n == 2:
      rslt = signUp()

  return rslt

def getProductList():
  productList = product_client.getProductList()
  for product in productList:
    print(product)
  return

def getCategory():
  categories = product_client.getCategory()
    
  print('--------카테고리 리스트-------')
  for c in categories:
    print(c)
  return

def addProduct():
  getCategory()

  categoryId = int(input('상품을 추가하고 싶은 카테코리의 id를 입력: '))
  productName = input('상품 이름 입력: ')
  description = input('상품 상세 설명 입력: ')
  price = float(input('상품 가격 입력: '))
  count = int(input('상품 재고 개수를 입력: '))

  product = { 
    'productId': 0,
    'categoryId': categoryId, 
    'name': productName, 
    'description': description, 
    'price': price,
    'count': count
  }
  product_client.createProduct(product)
  return

def findByCategory():
  getCategory()
  categoryId = int(input('조회하고 싶은 카테고리의 id값 입력: '))
  products = product_client.findByCategory(categoryId)

  for p in products:
    print(p)
  return

def buyProduct(userId):
  getProductList()
  productName = input('구매하고 싶은 상품명 입력: ')
  count = int(input('개수 입력: '))
  
  purchase = {
    'productName': productName,
    'count': count,
    'userId': userId
  }

  product_client.buyProduct(purchase)
  return

def findByName():
  name = input('상품명 입력: ')
  result = product_client.findByName(name)
  print(result)
  return

def getPurchase(userId):
  purchaseList = product_client.getPurchase(userId)
  for purchase in purchaseList:
    print(purchase)
  return

def selectMenu():
  print("select menu")
  print("-------------------")
  print("0: 물품 목록 조회")
  print("1: 물품 추가하기")
  print("2: 카테고리로 물품 찾기")
  print("3: 물품 구매")
  print("4: 이름으로 물품 찾기")
  print("5: 구매기록 조회")
  print("6: 로그아웃")
  print('-1: 프로그램 끝내기')
  print("-------------------\n")
  print("번호 입력: ")
  num = int(input())

  return num

def start():
  userId = userController()

  num = selectMenu()
  while num != -1:
    if num == 0:
      getProductList()
    elif num == 1:
      addProduct()
    elif num == 2:
      findByCategory()
    elif num == 3:
      buyProduct(userId)
    elif num == 4:
      findByName()
    elif num == 5:
      getPurchase(userId)
    elif num == 6:
      logout()
      userId = userController()
    elif num == -1:
      return
    
    num = selectMenu()

if __name__ == "__main__":
  print("program start...\n")
  start()