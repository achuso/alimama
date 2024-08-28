const mongoose = require('mongoose');

// Connect to MongoDB
const mongoURI = 'mongodb://localhost:27017/alimama-mongodb';
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
}

initDatabase();

module.exports = { Item, Review };
