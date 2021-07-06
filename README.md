# Tiden

### Accept payments as interest-earning USDC from anywhere in the world.

http://www.trytiden.com | HackMoney 2021 

🎥 [Demo](https://www.youtube.com/watch?v=d6pP91_KAR4) | 🎬 [Hackathon Showcase](https://showcase.ethglobal.co/hackmoney2021/tiden)


<img src="https://github.com/jw122/tiden/blob/main/assets/landing-page.png" width="600" />

## Background
We envision a world where merchants can accept payments from any country, in the form of a currency as powerful and valuable as the US Dollar. We also want to empower more online businesses and creators by boosting their earnings with APY and passive income as soon as they are received.

Unfortunately, we have not yet identified strong existing tools that address these core issues:

1. Easily accepting payments from credit cards around the world, and settling in USD-pegged stablecoins (especially relevant in developing economies)
2. Seamless integration flow where a component to accept payments that can easily be injected into the storefront
3. Management of payments received: transferring to fiat bank account, other crypto wallet, income analytics

The existing products that do come somewhat close to this still charge high fees that most online merchants cannot afford. But one tool we found promising to build Tiden with was Circle. So which we built an e2e integration with it during this hackathon to prototype the ultimate component for accepting crypto payments.

On top of easily embedding a checkout and payment-receiving experience, we also want to go a step further and leverage the power of DeFi to earn stable yield for merchants who use Tiden.

## How It's Made
<img src="https://github.com/jw122/tiden/blob/main/assets/payment.png" width="600" />


Payment systems are the lifeblood of businesses and are expected to be robust with high uptimes, this is why we chose industry-grade technologies and frameworks to build, deploy, and host our systems.

For our core business logic, we integrated heavily with Circle.

We used various Circle API endpoints, including the Payments API, core functionality such as encryption, and the Accounts API.

We built a complete, end-to-end flow where the card details of a user are encrypted in compliance with the PCI data security standard. A payment request with the user's fiat payment is made through the Circle API, which processes the transaction and we fund the merchant's account with USDC.


To allow a customer of Tiden to transfer their funds into other crypto wallets or bank accounts, we made user of Circle's Transfers API. In the event of a transfer of USDC from a customer's Tiden account to a crypto wallet, a transaction hash is made available to the customer.

<img src="https://github.com/jw122/tiden/blob/main/assets/dashboard.png" width="600" />

<img src="https://github.com/jw122/tiden/blob/main/assets/transfer.png" width="600" />

We also took the infrastructure to the next level: Instead of using an entry-level host solution like Heroku, we used AWS ECR to host our server images, and used AWS App Runner to allow for continuous deployment, dynamic server scaling, and resource management of the production Kotlin server. We used AWS S3 and CloudFront to host our static website, which allows for smart routing to the closest edge servers that speed up our website's distribution.

We choose Kotlin along with the Ktor web framework to take advantage of the wide support and performance characteristics for JVM languages. We explicitly chose compiled typesafe languages for the server to increase the robustness of our systems. The server code is dockerized for deployment, which means we'll never run into issues on production due to environment mismatch. For our database we're using the latest distribution of PostgreSQL, running on AWS RDS in production. We set up a dockerized test DB of Postgres for local development which automatically applies any DDL and DMLs from our migrations directory.

We built our webapp using React Typescript, again to ensure maximum type safety and fault tolerance. We minimized logic on the client-side for increased security and improved performance.

We chose to keep the Kotlin backend and React frontend completely separate instead of using existing (but new) solutions like Kotlin Multiplatform or Kotlin/JS. This way, we could use the best of both worlds in their most mature/stable forms instead of taking on a risk with libraries in alpha or with low adoption ([more here](https://www.reddit.com/r/Kotlin/comments/kqw0je/how_to_start_with_ktor_react/)).

We pride ourselves on building a production-grade application with industry-leading infrastructure.

<img src="https://github.com/jw122/tiden/blob/main/assets/architecture.png" width="600" />

## getting started
`source .env`

## deployment
### frontend
from the root folder
```bash
yarn --cwd ./frontend/ build && aws s3 sync ./frontend/build/ $S3_BUCKET_HOSTED_SITE
```

For more instructions for the backend, see ./server/README.md
