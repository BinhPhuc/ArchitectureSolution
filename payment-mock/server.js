const express = require('express');
const { randomUUID } = require('node:crypto');
const { STATUS_CODES } = require('node:http');

const app = express();
const payments = new Map();

app.use(express.json());

function sendError(req, res, status, message) {
  return res.status(status).json({
    status_code: status,
    error: STATUS_CODES[status],
    message,
    path: req.path,
    timestamp: new Date().toISOString(),
  });
}

app.get('/health', (req, res) => {
  res.json({ status: 'UP' });
});

app.post('/payments', (req, res) => {
  const { customer_id: customerId, total_amount: totalAmount } = req.body ?? {};

  if (typeof customerId !== 'string' || !customerId.trim()) {
    return sendError(req, res, 400, 'customer_id must be a non-empty string');
  }
  if (typeof totalAmount !== 'number' || !Number.isFinite(totalAmount) || totalAmount <= 0) {
    return sendError(req, res, 400, 'total_amount must be a positive number');
  }
  const fault = req.get('Fault')?.toLowerCase() ?? 'false';
  if (!['true', 'false'].includes(fault)) {
    return sendError(req, res, 400, 'Fault must be true or false');
  }

  if (fault === 'true') {
    return sendError(req, res, 422, 'Payment failed: unable to debit customer account');
  }

  const payment = {
    transaction_id: randomUUID(),
    customer_id: customerId.trim(),
    total_amount: totalAmount,
    status: 'SUCCESS',
    created_at: new Date().toISOString(),
  };
  payments.set(payment.transaction_id, payment);

  return res.json({
    status_code: 200,
    message: 'Payment successful',
    data: { transaction_id: payment.transaction_id, status: payment.status },
  });
});

app.use((req, res) => sendError(req, res, 404, 'Endpoint not found'));

app.use((err, req, res, next) => {
  const status = err.status >= 400 && err.status < 500 ? err.status : 500;
  return sendError(req, res, status, status === 500 ? 'Internal server error' : err.message);
});

if (require.main === module) {
  const port = Number(process.env.PORT || 3001);
  app.listen(port, () => {
    console.log(`Payment mock server listening on port ${port}`);
  });
}

module.exports = app;
