# alimama
e-commerce website for my internship at Deka Technology

## **Prerequisites**
- Docker
- Docker Compose

## **Usage**
1. Clone repository:
```bash
git clone https://github.com/your-username/alimama.git
cd alimama
```
2. Set up the .env file:
```bash
cp env_template.env .env
```
3. Start all services with:
```bash
docker-compose up -d
```
4. Access website at `http://localhost:3000`
5. Stop all running containers with:
```bash
docker-compose down
```
## **To-Do**
- [ ] Implement review and purchasing logic
- [ ] Add account page to change credentials
- [ ] Support images for products
- [ ] Fix bandaid page reloading soln. for cart interactions
- [ ] Get a cease-and-desist from Alibaba
