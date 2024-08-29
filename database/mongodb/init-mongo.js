// Import env vars
const path = require('path');
const dotenv = require('dotenv');
dotenv.config({ path: path.resolve(__dirname, '../../.env') });

const mongo_username = process.env.MONGO_INITDB_ROOT_USERNAME;
const mongo_password = process.env.MONGO_INITDB_ROOT_PASSWORD;
const mongo_dbname = process.env.MONGO_DB_NAME;
const mongo_host = process.env.MONGO_HOST;
const mongo_port = process.env.MONGO_PORT;

// Connect to MongoDB
const mongoose = require('mongoose');
const mongoURI = `mongodb://${mongo_username}:${mongo_password}@${mongo_host}:${mongo_port}/${mongo_dbname}?authSource=admin`;

// Example output
mongoose.set('strictQuery', true);
mongoose.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true });

// Tags as enum
const tagsEnum = ['Laptop', 'Unisex T-Shirt', 'Jean', 'Watch', 'Tea'];

// Item schema
const itemSchema = new mongoose.Schema({
  productName: { type: String, required: true },
  vendorId: { type: mongoose.Schema.Types.ObjectId, ref: 'Vendor', required: true }, // from relational db
  numInStock: { type: Number, required: true },
  price: { type: Number, required: true },
  pictures: { 
    type: [String], 
    validate: {
      validator: function(v) {
        return v.length <= 3;
      },
      message: 'Max 3 pics allowed for {PATH}'
    },
    default: [] 
  },
  tags: { type: [String], enum: tagsEnum, default: [] },
  ratingAvgTotal: { type: Number, default: 0 }
});

// Cart x Item schemas
const cartItemSchema = new mongoose.Schema({
  item_id: { type: mongoose.Schema.Types.ObjectId, ref: 'Item', required: true },
  unit_price: { type: Number, required: true },
  quantity: { type: Number, required: true }
});

// Cart schema
const cartSchema = new mongoose.Schema({
  _id: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  items: [cartItemSchema],
  total_amount: { type: Number, default: 0 },
  created_at: { type: Date, default: Date.now }
});

// Purchase x Item schema (for multiple items in purchase)
const purchaseItemSchema = new mongoose.Schema({
  item_id: { type: mongoose.Schema.Types.ObjectId, ref: 'Item', required: true },
  unit_price: { type: Number, required: true },
  quantity: { type: Number, required: true }
});

// Purchase schema 
const purchaseSchema = new mongoose.Schema({
  _id: { type: mongoose.Schema.Types.ObjectId, required: true },
  user_id: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
  items: [purchaseItemSchema],
  total_amount: { type: Number, required: true },
  purchase_date: { type: Date, default: Date.now },
  payment_status: { type: String, default: 'Pending' }
});

// Review schema
const reviewSchema = new mongoose.Schema({
  productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Item', required: true },
  rating: { type: Number, min: 0, max: 10, required: true },
  text: { type: String, required: true },
  likes: { type: Number, default: 0 },
  dislikes: { type: Number, default: 0 },
  reviewedAt: { type: Date, default: Date.now }
});

// Create models
const Item = mongoose.model('Item', itemSchema);
const Review = mongoose.model('Review', reviewSchema);
const Cart = mongoose.model('Cart', cartSchema);

// sample data
async function initDatabase() {
  try {
    const newItem = new Item({
      productName: 'Sample Product',
      vendorId: new mongoose.Types.ObjectId(),
      numInStock: 10,
      price: 99.99,
      pictures: ['pic1.jpg', 'pic2.jpg'],
      tags: ['Tea'],
      ratingAvgTotal: 4.5
    });

    await newItem.save();

    const newReview = new Review({
      productId: newItem._id,
      rating: 5,
      text: 'Great product!',
      likes: 10,
      dislikes: 1,
      reviewedAt: new Date()
    });

    await newReview.save();

    console.log('DB init successful');
  } 
  catch (error) {
    console.error('DB init failed', error);
  }
  finally {
    mongoose.connection.close(() => {
      console.log('Mongo connection closed');
      process.exit(0);
    });
  }
}

initDatabase();

module.exports = { Item, Review };
